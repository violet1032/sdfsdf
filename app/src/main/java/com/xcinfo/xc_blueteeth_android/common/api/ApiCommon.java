package com.xcinfo.xc_blueteeth_android.common.api;

import org.kymjs.kjframe.utils.StringUtils;



/**
 * <p/>
 * description:
 * <p/>
 * author:zipeng
 * <p/>
 * createTime:2015/9/14 21:00
 * <p/>
 * version:1.0
 */
public class ApiCommon {
    private static final String TAG = "ApiCommon";

    /**
     * @param url   图片地址
     * @param img   图片控件
     * @param thumb 是否获取缩略图
     */
//    public static void getNetBitmap(String url, final ImageView img, boolean thumb) {
//        // 色块
//        final int i = img.getId() % 11;
//        if (StringUtils.isEmpty(url)) {
//            img.setImageResource(R.color.color_1 + i);
//            return;
//        }
//        // 如果地址没有域名，则给地址加上域名
//        url = StringUtils.getImgHttpUrl(url, thumb);
//        Log.e(TAG, "url:::" + url);
//        BitmapCallBack callBack = new BitmapCallBack() {
//            @Override
//            public void onPreLoad() {
//                super.onPreLoad();
//                img.setImageResource(R.color.color_1 + i);
//            }
//
//            @Override
//            public void onFailure(Exception e) {
//                super.onFailure(e);
//                img.setImageResource(R.color.color_1 + i);
//            }
//        };
//        AppContext.bitmap.display(img, url);
//
//    }


    /**
     * 获取闪频图片
     *
     * @param path
     * @return
     */
//    public static void getSplashPic(final String path, final ImageView imageView) {
//        AsyncTask task = new AsyncTask() {
//            @Override
//            protected Object doInBackground(Object[] params) {
//                Bitmap bitmap = null;
//                // 获取地址的用户名
//                String name = getImgName(path);
//                // 查看图片本地是否存在
//                // 判断文件是否存在
//                String str = AppConfig.getSaveImagePath()
//                        + name;
//                File file = new File(str);
//                if (file.exists()) {
//                    // 存在
//                    if (AppConfig.DEBUG)
//                        Log.e("", "图片存在");
//
//                    bitmap = BitmapFactory.decodeFile(str);
//                } else {
//                    //传入网络图片地址
//                    if (AppConfig.DEBUG)
//                        Log.e("", "图片不存在");
//                    try {
//                        URL url = new URL(StringUtils.getImgHttpUrl(path,
//                                false));
//                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                        conn.setRequestMethod("GET");
//                        conn.setConnectTimeout(5 * 1000);
//                        conn.connect();
//                        InputStream in = conn.getInputStream();
//                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//                        byte[] buffer = new byte[1024];
//                        int len = 0;
//                        while ((len = in.read(buffer)) != -1) {
//                            bos.write(buffer, 0, len);
//                        }
//                        byte[] dataImage = bos.toByteArray();
//                        bos.close();
//                        in.close();
//                        bitmap = BitmapFactory.decodeByteArray(dataImage, 0, dataImage.length);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    try {
//                        ImageUtils.saveImageToSD(AppContext.appContext, AppConfig
//                                .getSaveImagePath() + name, bitmap, 100);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//                return bitmap;
//            }
//
//            @Override
//            protected void onPostExecute(Object o) {
//                super.onPostExecute(o);
//                if (o != null)
//                    imageView.setImageBitmap((Bitmap) o);
//            }
//        };
//        task.execute();
//    }

    /**
     * 上传图片
     *
     * @param file         图片文件
     * @param RequestURL   上传地址
     * @param key          上传类型：头像：avatar、订单图片：orderpic
     * @param httpCallBack 上传后的回调函数
     */
  /*  public static void uploadPicture(File file, String RequestURL, String key, FHttpCallBack
            httpCallBack) {
        Bitmap bitmap = ImageUtils.getCompressBitmap(file.getPath());
        File fileTemporary = new File(AppConfig.getSaveImagePath() + "pic.png");
        UIHelper.compressBmpToFile(bitmap, fileTemporary);
        FileImageUpload.uploadFile(fileTemporary, RequestURL, key, httpCallBack);
    }*/

    /**
     * 上传图片
     *
     * @param file         图片文件
     * @param httpCallBack 上传后的回调函数
     */
   /* public static void uploadPicture(File file, FHttpCallBack httpCallBack) {
        uploadPicture(file, URLs.AVATAR_UPLOAD, "avatar", httpCallBack);
    }
*/
    /**
     * 上传discuz图片
     *
     * @param file         图片文件
     * @param httpCallBack 上传后的回调函数
     */
  /*  public static void uploadDiscuzPicture(File file, FHttpCallBack httpCallBack) {
        uploadPicture(file, URLs.DISCUZ_AVATAR_UPLOAD+AppConfig.getInstance().getmPre().getInt("discuz_duid",0), "Filedata", httpCallBack);
    }*/

    /**
     * 传入地址获取图片名称
     *
     * @param url
     * @return
     */
    private static String getImgName(String url) {
        String imgName = null;
        if (!StringUtils.isEmpty(url)) {
            // 截取地址最后的图片名
            int start = 0;
            while (url.indexOf("/", start + 1) > 0) {
                start = url.indexOf("/", start + 1);
            }
            imgName = url.substring(start + 1);
        }
        return imgName;
    }
}
