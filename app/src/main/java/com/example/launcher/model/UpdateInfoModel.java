/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.launcher.model;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.util.Log;

import com.example.launcher.util.IconCache;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represents an app in AllAppsView.
 */
public class UpdateInfoModel {
    //app名字
    private String appname;
    //服务器版本
    private int serverVersion;
    //服务器标志
    private int serverFlag;
    //强制升级
    private int lastForce;
    //app最新版本地址
    private String updateurl;
    //升级信息
    private String upgradeinfo;

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public String getAppname() {
        return this.appname;
    }

    public void setServerVersion(int serverVersion) {
        this.serverVersion = serverVersion;
    }

    public int getServerVersion() {
        return this.serverVersion;
    }

    public void setServerFlag(int serverFlag) {
        this.serverFlag = serverFlag;
    }

    public int getServerFlag() {
        return this.serverFlag;
    }

    public void setLastForce(int lastForce) {
        this.lastForce = lastForce;
    }

    public int getLastForce() {
        return this.lastForce;
    }

    public void setUpdateurl(String updateurl) {
        this.updateurl = updateurl;
    }

    public String getUpdateurl() {
        return this.updateurl;
    }

    public void setUpgradeinfo(String upgradeinfo) {
        this.upgradeinfo = upgradeinfo;
    }

    public String getUpgradeinfo() {
        return this.upgradeinfo;
    }
}