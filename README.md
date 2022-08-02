# Global-Images-Getter
## Fast, Simple, Easy to Use
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
  implementation 'com.github.Irfan-Karim:Global-Image-Getter:1.0' 
}
```

## Disclaimer
Get read write permission or Manage all storage permission before initialization else fetched folder and images will be null

## Fetch All Images

### Create Instance of ImageFetcher

```
val imageFetcher = ImageFetcher(context)
```

### Call getAllImages to get all image files in device
```
CoroutineScope(Dispathers.IO).launch {
  imageFetcher.getAllImages { file ->
    Log.i("TAG", "getImages: ${file?.size}")
  }
}
```
### Sort The Images
use ImageSortOrder._ as followed
```
CoroutineScope(Dispathers.IO).launch {
  imageFetcher.getAllImages(ImagesSortOrder.LastModifiedAscending) { file ->
    Log.i("TAG", "getImages: ${file?.size}")
  }
}
```

## Fetch All Folders Containing Images

### Create Instance of ImageFetcher

```
val imageFetcher = ImageFetcher(context)
```

### Call getAllImages to get all image files in device
```
imageFetcher.getDataAndFolders { folder ->
  Log.i("TAG", "getImages: ${folder?.size}")
}
```
Folder will contain the name of the folder and all the images contained in that folder
```
folder.foreach { it ->
  log.i("TAG", ${it.name})
  log.i("TAG", ${it.data.size})
}
```

### Sort the Folders
Use FolderSortOrder._ as followed
```
CoroutineScope(Dispatchers.IO).launch {
  imageFetcher.getDataAndFolders(null,FolderSortOrder.LengthAscending) { folder ->
    Log.i("TAG", "getImages: ${folder?.size}")
  }
}
```
### Sort the both Folders and Images
Use ImagesSortOrder._ and FolderSortOrder._ as followed
```
CoroutineScope(Dispatchers.IO).launch {
  imageFetcher.getDataAndFolders(ImagesSortOrder.LastModifiedAscending,FolderSortOrder.LengthAscending) { folder ->
    Log.i("TAG", "getImages: ${folder?.size}")
  }
}
```
