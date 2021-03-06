package com.securify.securify.model.userModels;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;

import com.securify.securify.model.gameModels.PermissionModel;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "userPermission",foreignKeys = {@ForeignKey(entity = UserModel.class,
                                                                parentColumns = "id",
                                                                childColumns = "userId",
                                                                onDelete = CASCADE),
                                                    @ForeignKey(entity = PermissionModel.class,
                                                                parentColumns = "id",
                                                                childColumns = "gameId",
                                                                onDelete=CASCADE)}
                                    , indices = {   @Index("userId"),
                                                    @Index("gameId")})
public class UserPermissionModel extends UserGameModel {

    public UserPermissionModel(long userId,long gameId,boolean played,boolean succeeded){
        super(userId,gameId,played,succeeded);
    }
}
