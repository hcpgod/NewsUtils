package com.tools.utils;

import cn.hutool.core.collection.CollectionUtil;
import com.tools.Run;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

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
//    int i = (int) (1 + Math.random() * (agentList.size() + 1));
    int i = random.nextInt(agentList.size());
    return agentList.get(i);
  }

  public static boolean init() {
    try{
      HashSet<String> set = new HashSet<>();
      String path = new Run().getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
      String[] pathSplit = path.split("/");
      String jarName = pathSplit[pathSplit.length - 1];
      String jarPath = path.replace(jarName, "");
      String pathName=jarPath+"user_agent_list";
      File file = new File(pathName);
      FileInputStream fis = new FileInputStream(file);
      InputStreamReader inputStreamReader = new InputStreamReader(fis, "utf-8");
      BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
      String s = bufferedReader.readLine();
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
