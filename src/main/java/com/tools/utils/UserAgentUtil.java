package com.tools.utils;

import cn.hutool.core.collection.CollectionUtil;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

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
      URL user_agent_list = new UserAgentUtil().getClass().getResource("/user_agent_list");
      File file = new File(user_agent_list.getPath());
      Reader reader = new FileReader(file);
      BufferedReader bufferedReader = new BufferedReader(reader);
      String s = bufferedReader.readLine();
      System.out.println(bufferedReader.lines());
      while (s != null){
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
