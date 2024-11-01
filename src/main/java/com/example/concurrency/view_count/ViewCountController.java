package com.example.concurrency.view_count;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ViewCountController {

  private final ViewCountService viewCountService;

  public ViewCountController(ViewCountService viewCountService) {
    this.viewCountService = viewCountService;
  }

  @GetMapping("/increment-sleep")
  public String incrementSleep(@RequestParam Long id) throws InterruptedException {
    viewCountService.incrementSleep(id);
    return "";
  }

  @GetMapping("/increment")
  public String incrementSleep2(@RequestParam Long id) {
    viewCountService.incrementViewCount(id);
    return "";
  }

  @GetMapping("/increment-sleep-lock-sleep")
  public String incrementSleep3(@RequestParam Long id) throws InterruptedException {
    viewCountService.incrementViewCountWithLockSleep(id);
    return "";
  }

  @GetMapping("/increment-sleep-lock")
  public String incrementSleep4(@RequestParam Long id) {
    viewCountService.incrementViewCountWithLock(id);
    return "";
  }


  /**
   * redis
   */
  @GetMapping("/increment/{id}")
  public String increment(@PathVariable Long id) {
    viewCountService.incrementViewCountByRedis(id);
    return "View count incremented";
  }

 /* @GetMapping("/view-count/{id}")
  public Long getViewCount(@PathVariable Long id) {
    return viewCountService.getViewCount(id);
  }*/
}