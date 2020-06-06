package pl.itacademy.tictactoe.api;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/hello")
public class HelloWorldController {

    @GetMapping
    public ResponseEntity<HelloWorld> helloWorld(@RequestParam(required = false) String name, @RequestParam(required = false) String value) {
        if (name == null || value == null) {
            return ResponseEntity.ok(new HelloWorld("hello", "world"));
        }
        return ResponseEntity.ok(new HelloWorld(name, value));
    }

    @GetMapping("/{name}")
    public ResponseEntity<HelloWorld> helloName(@PathVariable("name") String name) {
        return ResponseEntity.ok(new HelloWorld("hello", name));
    }

    private static class HelloWorld {
        String name;
        String value;

        public HelloWorld(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
