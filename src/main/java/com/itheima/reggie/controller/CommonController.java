package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${regiee.path}")
    private String basePath;

    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){//MultipartFile对象的名字必须是file，需要鱼前端页面保持一致
        log.info(file.toString());
        String randomName=null;
        try {
        //1.生成一个随机的名称：如果文件名相同时，会出现文件覆盖的情况，所以我们使用随机生成的文件名，来保存文件
            //1.1先获取源文件的名称
            String originalFilename = file.getOriginalFilename();
            //1.2获取源文件的后缀
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            randomName = UUID.randomUUID().toString()+suffix;
            System.out.println(randomName);
        //2判断是否存在basePath
            File dir = new File(basePath);
            if (!dir.exists()){
                //2.1如果文件目录不存在的话，就创建一个目录
                dir.mkdirs();
            }
            //保存文件
            file.transferTo(new File(basePath+randomName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(randomName);
    }

    /**
     * 文件下载
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        //输入流。读取文件内容
        try {
            FileInputStream fileInputStream=new FileInputStream(new File(basePath+name));
            //构造输出流，将读到的内容显示到浏览器
            ServletOutputStream outputStream = response.getOutputStream();
            //设置响应回去的数据类型
            response.setContentType("image/jpeg");
            byte[] bytes = new byte[1024];
            int length=0;
            while((length=fileInputStream.read(bytes))!=-1){
                outputStream.write(bytes,0,length);
                outputStream.flush();
            }
            //关闭资源
            fileInputStream.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //输出流。将我们的文件协会我们的浏览器，展示图片
    }
}
