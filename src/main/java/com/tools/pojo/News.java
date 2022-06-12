package com.tools.pojo;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author hcp
 */
@Data
@EqualsAndHashCode(of = {"newsUrl"})
public class News {

  private String newsUrl;

  private String newsTitle;

  private String site;

  private String type;
}
