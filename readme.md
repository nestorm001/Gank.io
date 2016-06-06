# Introduction #
使用gank.io的API实现的一个material design的客户端

# 主要使用的第三方库：#
* io.reactivex:rxjava:1.1.1
* io.reactivex:rxandroid:1.1.0
* com.squareup.retrofit2:retrofit:2.0.0
* com.jakewharton:butterknife:7.0.1 
* com.squareup.picasso:picasso:2.5.2
* com.squareup.sqlbrite:sqlbrite:0.6.3
* jp.wasabeef:recyclerview-animators:2.2.3

# Issues To Fix #
* ~~收藏偶尔会失败（似乎是因为unsubscribe调用太早）~~
* ~~某些url可以一直返回（用一个丑陋的办法处理了）（又用优雅的方法搞了一遍）~~
* ~~收藏出错数组越界（似乎解决了，呵）~~
* ~~SwipeRefreshLayout经常会在第一次加载时不转~~
* ~~截图有问题，CardView的阴影消失了（放弃了截图）~~
* ~~使用新方法做滑动返回，滑动时按返回会出问题，跳转动画消失问题~~
* ~~可拖动区域过大(使用猥琐的方案解决了问题)~~

# TODO #
* video player
* 一堆想到还没想到的脑洞

# Release
* ~~[v0.1.0-beta ]()~~
* [v0.1.1-beta ](https://github.com/nestorm001/Gank.io/releases/download/v0.1.1-beta/nesto.gankio_release_v0.1.1-beta_2016-05-31_nesto.apk)
* [v0.1.2-beta ](https://github.com/nestorm001/Gank.io/releases/download/v0.1.2-beta/nesto.gankio_release_v0.1.2-beta_2016-06-02_nesto.apk)
* [v0.1.3-beta ](https://github.com/nestorm001/Gank.io/releases/download/v0.1.3-beta/nesto.gankio_release_v0.1.3-beta_2016-06-04_nesto.apk)