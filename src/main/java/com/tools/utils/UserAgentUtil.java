package com.tools.utils;

import cn.hutool.core.collection.CollectionUtil;

import java.io.*;
import java.net.URL;
import java.util.*;

import com.tools.Run;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author hcp
 */
public class UserAgentUtil {
  private static final List<String> agentList = new ArrayList<>();
  private static Random random = new Random();

  public static String getAgent(){
    if (CollectionUtil.isEmpty(agentList)){
      init();
    }
    int i = (int) (1 + Math.random() * (agentList.size() + 1));
    return agentList.get(i);
  }

  public static boolean init() {
    try{
      HashSet<String> set = new HashSet<>();

      System.out.println("开始读取配置");
      String path = new Run().getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
      String[] pathSplit = path.split("/");
      String jarName = pathSplit[pathSplit.length - 1];
      String jarPath = path.replace(jarName, "");
      String pathName=jarPath+"config.properties";
      Properties properties = new Properties();
      File file = new File(pathName);
      FileInputStream fis = new FileInputStream(file);
      InputStreamReader inputStreamReader = new InputStreamReader(fis, "utf-8");
      BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
      String s = bufferedReader.readLine();
      System.out.println(bufferedReader.lines());
      while (s != null){
        System.out.println(s);
        if (StringUtils.isNotEmpty(s)){
          set.add(s);
        }
        s = bufferedReader.readLine();
      }
      agentList.addAll(set);
      return true;
    }catch (Exception e){
      return false;
    }
  }

  public static Object getRandom() {
    return random;
  }
}
