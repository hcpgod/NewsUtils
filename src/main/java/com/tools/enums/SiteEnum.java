package com.tools.enums;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author hcp
 */
@Getter
@AllArgsConstructor
public enum SiteEnum {

  BINANCE("币安", "binance","数字货币及交易对上新","48","https://www.binance.com",50),

  HOO("虎符", "hoo","新币上线","51","https://help.hoorhi.shop",50),

  MEXC("MEXC", "mexc","新币上线","52","https://support.mexc.com",50);

   final String siteName;

   final String siteCode;

   final String typename;

   final String typeCode;

   final String domain;

   final Integer listSize;

   public static SiteEnum parseByUrl(String url){
       // 是否 虎符
       if(url.indexOf(HOO.getDomain())>-1){
           return HOO;
       }
       if (url.indexOf(MEXC.getDomain())>-1){
           return MEXC;
       }
       if (url.indexOf(BINANCE.getDomain())>-1){
           return BINANCE;
       }
       return null;
   }

}
