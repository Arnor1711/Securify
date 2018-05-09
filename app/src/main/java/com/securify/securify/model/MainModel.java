package com.securify.securify.model;

import android.content.Context;

import com.securify.securify.database.AppDatabase;
import com.securify.securify.database.PopulationFactory;
import com.securify.securify.database.daos.gameDaos.PasswordDao;
import com.securify.securify.database.daos.gameDaos.PermissionDao;
import com.securify.securify.database.daos.gameDaos.PhishingDao;
import com.securify.securify.database.daos.userDaos.UserDao;
import com.securify.securify.model.gameModels.PasswordModel;
import com.securify.securify.model.gameModels.PermissionModel;
import com.securify.securify.model.gameModels.PhishingModel;
import com.securify.securify.model.gameModels.UsedPasswordModel;
import com.securify.securify.model.gameModels.UsedPasswordUserModel;
import com.securify.securify.model.userModels.UserGameModel;
import com.securify.securify.model.userModels.UserModel;
import com.securify.securify.model.userModels.UserPasswordModel;
import com.securify.securify.model.userModels.UserPermissionModel;
import com.securify.securify.model.userModels.UserPhishingModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alwin on 06.05.2018.
 */

public class MainModel {
    private Context context;
    private AppDatabase db;
    private GamePicker gamePicker;

    private UserModel activeUser;

    public UserModel getActiveUser() {
        return activeUser;
    }

    public void setActiveUser(UserModel activeUser) {
        this.activeUser = activeUser;
    }

    public MainModel(Context context){
        this.context=context;

        db=AppDatabase.getDatabase(context);
        gamePicker=new GamePicker(context,db);
    }

    public PasswordModel getPassGameById(long id){
        return gamePicker.getPassGameById(id);
    }

    public PermissionModel getPermGameById(long id){
        return gamePicker.getPermGameById(id);
    }

    public PhishingModel getPhishGameById(long id){
        return gamePicker.getPhisGameById(id);
    }

    public void databaseinit(){
        PopulationFactory factory=new PopulationFactory();

        PasswordDao paDao=db.passwordDao();
        paDao.insertAll(factory.getPasswordModels());

        PhishingDao phDao=db.phishingDao();
        phDao.insertAll(factory.getPhishingModels());

        PermissionDao peDao=db.permissionDao();
        peDao.insertAll(factory.getPermissionModels());

        UserDao userDao=db.userDao();
        userDao.insertAll(factory.getUserModels());

        initUserGameTables(userDao,peDao,phDao,paDao);

    }

    private void initUserGameTables(UserDao userDao,PermissionDao permissionDao,PhishingDao phishingDao, PasswordDao passwordDao){
        List<UserModel> users=userDao.getAllUsers();

        List<PermissionModel> permissionModels=permissionDao.getAllPermissionGames();
        List<PasswordModel> passwordModels=passwordDao.getAllPasswordGames();
        List<PhishingModel> phishingModels=phishingDao.getAllPhishingGames();

        List<UserPermissionModel> userPermissionModels=new ArrayList<>();
        List<UserPasswordModel> userPasswordModels=new ArrayList<>();
        List<UserPhishingModel> userPhishingModels=new ArrayList<>();

        for(UserModel user:users){
            userPermissionModels.addAll(initUserWithPermissions(user,permissionModels));
            userPasswordModels.addAll(initUserWithPasswords(user,passwordModels));
            userPhishingModels.addAll(initUserPhishing(user,phishingModels));
        }

        db.userPermissionDao().insertAll(userPermissionModels);
        db.userPasswordDao().insertAll(userPasswordModels);
        db.userPhishingDao().insertAll(userPhishingModels);
    }



    private List<UserPermissionModel> initUserWithPermissions(UserModel user, List<PermissionModel> permissionModels){

        List<UserPermissionModel> insertList=new ArrayList<>();

        for(PermissionModel permissionModel:permissionModels){
            UserPermissionModel userPermissionModel=new UserPermissionModel(user.getId(),permissionModel.getId(),false);

            insertList.add(userPermissionModel);
        }

        return insertList;
    }


    private List<UserPhishingModel> initUserPhishing(UserModel user,List<PhishingModel> phishingModels){

        List<UserPhishingModel> insertList=new ArrayList<>();

        for(PhishingModel phishingModel:phishingModels){
            UserPhishingModel userPhishingModel=new UserPhishingModel(user.getId(),phishingModel.getId(),false);

            insertList.add(userPhishingModel);
        }
        return insertList;
    }


    private List<UserPasswordModel> initUserWithPasswords(UserModel user,List<PasswordModel> passwordModels){

        List<UserPasswordModel> inserList=new ArrayList<>();

        for(PasswordModel passwordModel:passwordModels){
            UserPasswordModel userPasswordModel=new UserPasswordModel(user.getId(),passwordModel.getId(),false);

            inserList.add(userPasswordModel);
        }
        return inserList;
    }


    /*
    private <UserGame,Game> List<UserGame> initUserWithGames(UserModel user, List<Game> games,UserGame tempUserGame){
        List<UserGame> insertList=new ArrayList<>();

        Class<UserGame> userGameClass;

        try{
            UserGame userGame = userGameClass.newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        for(Game game:games){
            UserGame userGame= UserGame;

        }
    }*/


    public boolean isPasswordUsed(long userId, String password){
        return db.usedPasswordUserDao().isPasswordUsedByUserId(password,userId);
    }

    public void setPasswordUsed(long userId, String password){
        long passwordId;
        if(db.usedPasswordDao().doesExist(password)){
            passwordId=db.usedPasswordDao().getUsedPasswordByPassword(password).getId();
        }
        else{
            UsedPasswordModel usedPasswordModel=new UsedPasswordModel(password);
            passwordId=db.usedPasswordDao().insertGetLong(usedPasswordModel);
        }
        UsedPasswordUserModel usedPasswordUserModel=new UsedPasswordUserModel(userId,passwordId);
        db.usedPasswordUserDao().insert(usedPasswordUserModel);
    }





    //return progress percentage of game
    private<T extends UserGameModel> int getGameProgress(List<T> list){

        int gamesCount=list.size();
        int gamesPlayed=0;
        for(T t:list){
            if(t.isPlayed()){
                gamesPlayed++;
            }
        }
        if(gamesCount==0) return 100;
        else return 100*gamesPlayed/gamesCount;
    }

    public int getPermissionProgress(long userId){
        List<UserPermissionModel> userPermissionModels=db.userPermissionDao().getUserGamesByUserId(userId);
        return getGameProgress(userPermissionModels);
    }
    public int getPassswordProgress(long userId){
        List<UserPasswordModel> userPasswordModels=db.userPasswordDao().getUserGamesByUserId(userId);
        return getGameProgress(userPasswordModels);
    }
    public int getPhishingProgress(long userId){
        List<UserPhishingModel> userPhishingModels=db.userPhishingDao().getUserGamesByUserId(userId);
        return getGameProgress(userPhishingModels);
    }


    //HIGHSCORE METHODS-------------------
    //methods to set user Highscores that also check if it is a new highscore, if it is not a new highscore nothing happens
    public void setUserPasswordHighscore(long highscore){
        UserModel user=getActiveUser();
        if(user.getPasswordHighscore()<highscore){
            user.setPasswordHighscore(highscore);
            db.userDao().update(user);
        }
    }
    public void setUserPhishingHighscore(long highscore){
        UserModel user=getActiveUser();
        if(user.getPhishingHighscore()<highscore){
            user.setPhishingHighscore(highscore);
            db.userDao().update(user);
        }
    }
    public void setUserPermissionHighscore(long highscore){
        UserModel user=getActiveUser();
        if(user.getPermissionHighscore()<highscore){
            user.setPermissionHighscore(highscore);
            db.userDao().update(user);
        }
    }



    //Methods for the Highscores, they com already ordered from the database
    public List<UserModel> getTopPassword(int top_count){
        return db.userDao().getTopPassword(top_count);
    }
    public List<UserModel> getTopPhishing(int top_count){
        return db.userDao().getTopPhishing(top_count);
    }
    public List<UserModel> getTopPermission(int top_count){
        return db.userDao().getTopPermission(top_count);
    }




    //NOT RELEVANT METHODS
    public PasswordModel getMaxPassGame(){
        PasswordDao dao=db.passwordDao();
        return dao.getMax();
    }


}
