package com.iecube.community.util;

import lombok.extern.slf4j.Slf4j;


import java.io.File;
@Slf4j
public class DeleteFolderUtils {
    public static Boolean deleteFile(File file) {
        //判断文件不为null或文件目录存在
        if (file == null || !file.exists()) {
            log.error("文件删除失败,请检查文件是否存在以及文件路径是否正确");
            return false;
        }
        //获取目录下子文件
        File[] files = file.listFiles();
        //遍历该目录下的文件对象
        for (File f : files) {
            //判断子目录是否存在子目录,如果是文件则删除
            if (f.isDirectory()) {
                //递归删除目录下的文件
                deleteFile(f);
            } else {
                //文件删除
                f.delete();
            }
        }
        //文件夹删除
        file.delete();
       log.info("目录名：" + file.getName());
        return true;
    }
}
