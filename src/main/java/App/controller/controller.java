package App.controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.PrintWriter;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;

@RestController
public class controller {

    //Для записи времени в формате ISO 801
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    //Для проверки валидности uid
    private static final Pattern pattern = Pattern
            .compile("(?i)^[0-9a-f]{8}-?[0-9a-f]{4}-?[0-5][0-9a-f]{3}-?[089ab][0-9a-f]{3}-?[0-9a-f]{12}$");

    private static final boolean isValidUuid(final String uuid) {
        return ((uuid != null) && (uuid.trim().length() > 31)) ? pattern.matcher(uuid).matches() : false;
    }

    //Для бд
    ApplicationContext context = new ClassPathXmlApplicationContext("context.xml");
    JdbcTemplate jdbcTemplate = context.getBean(JdbcTemplate.class);

    @RequestMapping(value = "/task", method = RequestMethod.POST)
    @ResponseBody
    void home(HttpServletResponse response) {
        Date date = new Date(System.currentTimeMillis());
        String time = sdf.format(date);
        //создание GUID
        UUID uid = UUID.randomUUID();
        jdbcTemplate.update(
                "INSERT INTO mts (id, status, timestamp) VALUES  (?, 'created' ,   ?)", uid,time);
        try {
            response.setStatus(202);
            PrintWriter out = response.getWriter();
            JSONObject obj = new JSONObject();
            obj.put("id", uid);
            out.println(obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
        date.setTime(System.currentTimeMillis());
        time = sdf.format(date);
        jdbcTemplate.update("UPDATE mts SET status = 'running',timestamp = ? WHERE id = ?", time, uid);
        TimerTask tt = new TimerTask() {

            @Override
            public void run() {
                date.setTime(System.currentTimeMillis());
                String time = sdf.format(date);
                jdbcTemplate.update(
                        "UPDATE mts SET status = 'finished',timestamp = ? WHERE id = ?", time, uid);
            }
        };
        new Timer().schedule(tt, 2*60 * 1000);
    }

    @RequestMapping(value = "/task/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
    public @ResponseBody
    ResponseEntity<Map<String, Object>> get(@PathVariable String id) {
        Map<String, Object> map = new HashMap<String, Object>();
//Проверка валидности uid
        if(isValidUuid(id))
            try {
                map = jdbcTemplate.queryForMap(
                        "Select status,timestamp FROM  mts WHERE id = ?", id);
                return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<Map<String, Object>>(map, HttpStatus.NOT_FOUND);
            }
        else
            return new ResponseEntity<Map<String, Object>>(map, HttpStatus.BAD_REQUEST);

    }
}