package hello;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Controller
public class GreetingController {

    private List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @RequestMapping("/greeting-sse")
    public SseEmitter greeting() throws Exception {
        SseEmitter e = new SseEmitter();
        e.onCompletion(() -> emitters.remove(e));
        emitters.add(e);
        return e;
    }

    @RequestMapping("/greeting/{name}")
    @ResponseBody
    public Greeting greeting(@PathVariable("name") String name) {
        Greeting greeting = new Greeting("Hello, " + name);
        for (SseEmitter event : emitters) {
            try {
                event.send(SseEmitter.event().name("message").data(greeting));
            } catch (Exception ex) {

            }
        }
        return greeting;
    }
}
