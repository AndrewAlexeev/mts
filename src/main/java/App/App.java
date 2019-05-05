package App;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import java.io.File;

@SpringBootApplication
public class App {
        public static void main(String[] args) throws Exception {
            File file = new File("Database.db");
//Создание таблицы если,если бд не существует
            if(!file.exists()){
                ApplicationContext context = new ClassPathXmlApplicationContext("context.xml");
                JdbcTemplate jdbcTemplate = context.getBean(JdbcTemplate.class);
                jdbcTemplate.execute("CREATE TABLE mts (id SERIAL, status VARCHAR(255), timestamp VARCHAR(255))");
                }
            //Запуск приложения
            SpringApplication.run(App.class, args);
        }
}
