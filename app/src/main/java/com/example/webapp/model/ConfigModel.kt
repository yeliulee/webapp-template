package com.example.webapp.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName


/**
 * 配置模型
 * @author yeliulee
 * Created at 2021/5/14 18:00
 */
class ConfigModel {
    /**
     * 配置示例
     * screenOrientation : vertical
     * hardwareAccelerated : false
     * pcMode : true
     * openAppEnable : false
     * supportMultipleWindows : true
     * enableProgressBar : false
     * launchUrl : https://www.baidu.com
     * theme : {"colorPrimary":"#79BD9A"}
     * appbar : {"title":"测试百度","showWebTitle":true,"menuEnable":true,"enable":true}
     */
    var landscape = false
    var hardwareAccelerated = false
    var pcMode = false
    var openAppEnable = false
    var supportMultipleWindows = false
    var enableProgressBar = true
    var launchUrl = ""
    var theme = ThemeConfigBean()
    var appbar = AppbarConfigBean()

    @Keep
    class ThemeConfigBean {
        /**
         * 主题配置
         * colorPrimary : #79BD9A
         */
        var colorPrimary = "#79BD9A"
    }

    @Keep
    class AppbarConfigBean {
        /**
         * 顶栏配置
         * title : 测试百度
         * showWebTitle : true
         * menuEnable : true
         * enable : true
         */
        var title = ""
        var showWebTitle = true
        var menuEnable = false
        var enable = true
    }
}