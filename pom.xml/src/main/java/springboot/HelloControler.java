package springboot;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloControler {

	@RequestMapping("/springboot/fremobile")
	public String freeMobile(@RequestParam(value="status", defaultValue="off") String status) {
		return "Status param is: " + status + "\n";
	}

}
