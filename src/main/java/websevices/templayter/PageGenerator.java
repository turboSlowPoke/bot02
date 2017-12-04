package websevices.templayter;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

public class PageGenerator {
    private static final String HTML_DIR = "templates";
    private static PageGenerator pageGenerator;
    private final Configuration cfg;

    public PageGenerator() {
        cfg =  new Configuration(Configuration.VERSION_2_3_23);
        try {
            cfg.setDirectoryForTemplateLoading(new File("./webcontent"));
            cfg.setDefaultEncoding("UTF-8");
        } catch (IOException e) {
            System.out.println(" не смог найти директорию templates ");
            e.printStackTrace();
        }
    }

    public static PageGenerator instance(){
        if (pageGenerator==null)
            pageGenerator = new PageGenerator();
        return pageGenerator;
    }

    public String getStaticPage(String fileName, Map<String,Object> dataMap){
        Writer writer = new StringWriter();
        try {
            Template template = cfg.getTemplate(fileName);
            template.process(dataMap,writer);
        } catch (IOException e) {
            System.out.println(" не смог найти файл "+fileName);
            e.printStackTrace();
        } catch (TemplateException e) {
            System.out.println(" не получилось обработать шаблон " + fileName);
            e.printStackTrace();
        }
        return writer.toString();

    }
}
