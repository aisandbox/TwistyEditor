package dev.aisandbox.twistyeditor;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConfigurationProperties(prefix = "editor")
@Data
public class Settings {

  private String buildDate;
  private String buildVersion;
}
