# Global-Image-Getter
Fetch all images along with folders containing images in Android device

## Import

### Add it in your root build.gradle at the end of repositories:

```
allprojects {
  repositories {
  ...
  maven { url 'https://jitpack.io' }
  }
}
```
### Add the dependency

```
dependencies {
  implementation 'com.github.Irfan-Karim:Global-Image-Getter:Tag' 
}
```

## Fetch All Images

### Create Instance of ImageFetcher

```
val imageFetcher = ImageFetcher(context)
```

### Call getAllImages to get all image files in device
```
CoroutineScope(Dispathers.IO).launch{
  imageFetcher.getAllImages { file->
    Log.i("TAG", "getImages: ${file?.size}")
  }
}
```
### Sort The Images
use ImageSortOrder._ as followed
```
CoroutineScope(Dispathers.IO).launch{
  imageFetcher.getAllImages(ImagesSortOrder.LastModifiedAscending) { file->
    Log.i("TAG", "getImages: ${file?.size}")
  }
}
```
