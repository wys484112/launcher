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

package com.example.launcher.fotaupdate.model;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.util.Log;

import com.example.launcher.R;
import com.example.launcher.util.IconCache;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represents an app in AllAppsView.
 */
public class UpdateInformation  {
    public static String appname = "";
    public static int localVersion = 1;// 本地版本
    public static String versionName = ""; // 本地版本名
    public static int serverVersion = 1;// 服务器版本
    public static int serverFlag = 0;// 服务器标志
    public static int lastForce = 0;// 之前强制升级版本
    public static String updateurl = "";// 升级包获取地址
    public static String upgradeinfo = "";// 升级信息
    public static String downloadDir = "wuyinlei";// 下载目录
}