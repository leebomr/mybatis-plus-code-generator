package com.fengwenyi.codegenerator.controller;

import com.fengwenyi.api.result.ResponseTemplate;
import com.fengwenyi.apistarter.annotation.IgnoreResponseAdvice;
import com.fengwenyi.codegenerator.Config;
import com.fengwenyi.codegenerator.config.ErwinProperties;
import com.fengwenyi.codegenerator.service.IIndexService;
import com.fengwenyi.codegenerator.vo.CodeGeneratorRequestVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * @author <a href="https://www.fengwenyi.com">Erwin Feng</a>
 * @since 2021-07-12
 */
@Controller
public class IndexController {

    private IIndexService indexService;
    private ErwinProperties erwinProperties;

    @RequestMapping("/")
    public String index(Model model) {
        model.addAttribute("version", erwinProperties.getApp().getVersion());
        return "index";
    }

    @PostMapping("/code-generator")
    @ResponseBody
    public ResponseTemplate<Void> codeGenerator(@RequestBody @Validated CodeGeneratorRequestVo requestVo) {

        return indexService.codeGenerator(requestVo);
    }

    @RequestMapping("/download")
    public String fileDownLoad(HttpServletResponse response) {
        //下载生成文件
        String outDir = Config.OUTPUT_DIR;
        File file = new File(outDir+".zip");
        if (!file.exists()) {
            return "下载文件不存在";
        }
        response.reset();
        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("utf-8");
        response.setContentLength((int) file.length());
        response.setHeader("Content-Disposition", "attachment;filename=code-generator.zip");

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));) {
            byte[] buff = new byte[1024];
            OutputStream os = response.getOutputStream();
            int i = 0;
            while ((i = bis.read(buff)) != -1) {
                os.write(buff, 0, i);
                os.flush();
            }
        } catch (IOException e) {
            return "下载失败";
        }
        return "下载成功";
    }

    @Autowired
    public void setIndexService(IIndexService indexService) {
        this.indexService = indexService;
    }

    @Autowired
    public void setErwinProperties(ErwinProperties erwinProperties) {
        this.erwinProperties = erwinProperties;
    }

    @GetMapping("/upgrade")
    @ResponseBody
    @IgnoreResponseAdvice
    private String upgrade() {
        return indexService.upgrade(erwinProperties.getApp().getVersion());
    }
}
