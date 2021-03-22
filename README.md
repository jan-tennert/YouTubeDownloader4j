# YouTubeDownloader4j
With this dependency you can download youtube, twitter, twitch videos etc. in java/kotlin

# DISCLAIMER

To use this dependency you __need__ [YouTube-DL](https://youtube-dl.org) in the path or you can set the custom executable with ytdl.setCustomYTDL().\
For a few options you need [FFMPEG](https://www.ffmpeg.org) and/or [AtomicParsley](http://atomicparsley.sourceforge.net)

# Demo
Example in java (kotlin is much easier)
```java
YouTubeDownloader ytdl = new YouTubeDownloader();
String url = "https://www.youtube.com/watch?v=dQw4w9WgXcQ";

//Custom formats (you can ignore this if you just want the highest quality or only audio
VideoFormat[] format = ytdl.getFormats(url); /*With this you get every format. Every format has an extension like m4a or mp4 and a
                                              resolution like 720p, 360p etc.*/
VideoFormat[] format = ytdl.getFormats(url, false, new Function1<VideoFormat, Boolean>() {
     public Boolean invoke(VideoFormat videoFormat) {
          return videoFormat.getFormat_note().equals("720p");
     }
}); //The second argument "removeDuplicates just removes the duplicates from the list. E.g. multiple 720p formats can be in the list.
            //In the third argument and the function invoke you can enter what the video format must be like. Like a filter
VideoFormat format = ytdl.getFormat(url, "720p", null); //Here you can get first format which has the given resolution and extension (extension is nullable)

YouTubeDownloader.Builder video = new YouTubeDownloader.Builder(url)
          .directory(new File("/path/to/the/directory")) //optional
          .fileName("Video.mp4") //optional
           //Then one of these three:
          .highestQuality() //This just selects the highest quality + audio. Recommended
          .onlyAudio(false, false) //Use this if you want to download just the audio of the video
           //For the first and second argument you need ffmpeg and atomicparsley.
          .format(format[number]); //Custom format see above
        
 ytdl.downloadVideo(video); //Download the video
        
 ytdl.downloadVideo(video, new DownloadListener() {
    public void onDownloading(float progress, long eta) {
                System.out.println("Progress " + progress + "% / ETA: " + eta + "s");
    }

    public void onFinish() {
                System.out.println("Download finished");
    }
 });
```

# Installation

## Maven
```xml
<repositories>
	<repository>
	    <id>jitpack.io</id>
	    <url>https://jitpack.io</url>
	</repository>
</repositories>
```

```xml
<dependency>
    <groupId>com.github.jan-tennert</groupId>
    <artifactId>YouTubeDownloader4j</artifactId>
    <version>1.0</version>
</dependency>
```

## Gradle

```gradle
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

```gradle
dependencies {
     implementation 'com.github.jan-tennert:YouTubeDownloader4j:1.0'
}
```
