#MusicPlayerProxy
一个Android音乐播放器代理的实例。  
MediaPlayer的缓存大小是无法修改的，缓存文件是无法得到的。  
而在Android4.0之后，系统把缓存调节到了一个较大值，导致在移动网络下onPrepare时间过长。  
同一首音频在重复听或者seek时会多次发请求，不会缓存下来，导致浪费流量。  
   
本演示项目实现了一个预缓存、缓存机制。在播放前，可以把音频预缓存，   
音频听过一次后，会缓存下来，重复播放时在本地读取本地文件，不会发送请求。   
    
参考：    
http://stackoverflow.com/questions/4413300/change-buffer-size-on-mediaplayer    
http://stackoverflow.com/questions/10060165/android-mediaplayer-buffer-size-in-ics-4-0   
https://code.google.com/p/npr-android-app/source/browse/Npr/src/org/npr/android/news/StreamProxy.java    
http://blog.csdn.net/hellogv/article/category/1198699   （推荐大家看下他的博客）

欢迎+koukou:(537)(597)(299)讨论交流。（注明MediaPlayerProxy）