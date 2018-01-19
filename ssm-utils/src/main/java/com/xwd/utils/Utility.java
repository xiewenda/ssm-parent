package com.xwd.utils;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public final class Utility {
    private final static Logger logger = LoggerFactory.getLogger(Utility.class);

    public final static String TIME_FORMAT_YYYY_MM_DD = "yyyy-MM-dd";

    public static final int ONE_DAY = 24 * 60 * 60;

    private static final DecimalFormat SIMPLE_DECIMAL_FORMAT = new DecimalFormat("#0.00");

    /**
     * format time to "hh:mm:ss"
     * @param time
     * @return
     */
    public static String toTime(int time) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0)
            return "00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }
    public static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }

    public static boolean isEmpty(CharSequence str) {
        if (str == null || str.length() == 0)
            return true;
        else
            return false;
    }
    /**
     *
     * @param timePoint  时间戳 精确到秒
     * @return  返回日期到日  例如：2017-11-22
     */
    public static String getDateToDay(int timePoint) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timePoint * 1000L);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return year+"-"+month+"-"+day;
    }

    /**
     * format time to "yyyy-MM-dd"
     * @param time
     * @return
     */
    public static String formatTime(int time){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(time);
    }

    /**
     * get the day end time by given the time point
     * e.g. given timePoint is "2015.5.13 13:45:32", the returned day end time is "2015.5.13 23:59:59"
     * @param timePoint
     * @return
     */
    public static int getDayEndTime(int timePoint){
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        long theTime = ((long)timePoint)*1000;
        calendar.setTimeInMillis(theTime);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        calendar.clear();
        calendar.set(year, month, day, 23, 59, 59);
        int ret = (int)(calendar.getTimeInMillis()/1000);
        return ret;
    }

    public static int getDayStartTime(int timePoint) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        long theTime = ((long)timePoint)*1000;
        calendar.setTimeInMillis(theTime);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
//        int hour = calendar.get(Calendar.HOUR_OF_DAY);
//        int minute = calendar.get(Calendar.MINUTE);
//        int second = calendar.get(Calendar.SECOND);
        calendar.clear();
        calendar.set(year, month, day, 0, 0, 0);
        int ret = (int)(calendar.getTimeInMillis()/1000);
        return ret;
    }

    public static int getLastHourEndTime(int timePoint) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd-HH");
        String curHour = sf.format(System.currentTimeMillis());
        int ret = timePoint;
        try {
            ret = (int)((sf.parse(curHour).getTime())/1000);
        } catch(ParseException ex) {
            ex.printStackTrace();
        }
        return ret - 1;
    }


    /**
     * Mask the bankcard Number, e.g. 18911113927 to 189******3927
     * @param bankCardNo
     * @return masked phone number
     */
    public static String maskBankCard(String bankCardNo) {
        if(bankCardNo.length() <= 8) {
            return bankCardNo;
        }
        return bankCardNo.substring(0,4) + "******" + bankCardNo.substring(bankCardNo.length() - 4, bankCardNo.length());
    }

    /**
     * Mask the user name, e.g. 张小�???to **�???
     * @param userName
     * @return masked phone number
     */
    public static String maskUserName(String userName) {
        switch (userName.length()) {
            case 0:
            case 1:
                return "*";
            default:
                return userName.replaceAll(".", "*").replaceFirst(".$",userName.substring(userName.length() - 1));
        }
    }

    public static String maskPhoneNum(String phoneNum) {
        if(phoneNum==null || phoneNum.length() < 4) {
            return phoneNum;
        }
        return phoneNum.substring(0,3) + "****" + phoneNum.substring(phoneNum.length() - 4, phoneNum.length());
    }

    /**
     * Convert current time into int type. used for create_time/update time
     * @param
     * @return timestamp
     */
    public static int getCurrentTimeStamp() {
        if(debugCurrentTimeStamp == 0) {
            return (int) (System.currentTimeMillis() / 1000);
        }else {
            return debugCurrentTimeStamp;
        }
    }

    private static int debugCurrentTimeStamp = 0;

    /**
     * For test purpose, getCurrentTimeStamp will return the timestamp if non-zero.
     * @param
     * @return timestamp
     */
    public static void setDebugCurrentTimeStamp(int timeStamp) {
        debugCurrentTimeStamp = timeStamp;
    }


    public static String generateInvitationCode(Set<String> existingCode){
        int invitationCode = (int)((Math.random()*9+1)*100000);
        if(existingCode != null){
            while(existingCode.contains(String.valueOf(invitationCode))){
                invitationCode++;
                if(invitationCode>=999999){
                    invitationCode = (int)((Math.random()*9+1)*100000);
                }
            }
        }
        return String.valueOf(invitationCode);
    }


    public static String getDataFormatString(int currentSec) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sf.format(((long) currentSec) * 1000);
    }

    /**
     * 获取本月第一天的日期
     */
    public static int findMonthBeginTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, 0);
        c.set(Calendar.DAY_OF_MONTH, 1);//设置为1号,当前日期既为本月第一天
        String first = format.format(c.getTime());
        try {
            Date date = format.parse(first);
            return (int) (date.getTime() / 1000);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return getCurrentTimeStamp();
    }

    public static int findLastMonthBeginTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, -1);
        c.set(Calendar.DAY_OF_MONTH, 1);//设置为1号,当前日期既为本月第一天
        String first = format.format(c.getTime());
        try {
            Date date = format.parse(first);
            return (int) (date.getTime() / 1000);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return getCurrentTimeStamp();
    }

    public static boolean isMobile(String str) {
        boolean b = false;
        Pattern p = Pattern.compile("^[1][0-9]{10}$"); // 验证手机号
        Matcher m = p.matcher(str);
        b = m.matches();
        return b;
    }

    /**
     * 获得指定日期的年月日
     * @param targetTime
     * @return
     */
    public static Map<String,Integer> getYearAndMonthAndDay(Long targetTime){
        Map<String,Integer> map = new HashMap<String,Integer>();
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(targetTime));
        int year=cal.get(Calendar.YEAR);//得到年
        int month=cal.get(Calendar.MONTH);//得到月，因为从0开始的
        int day=cal.get(Calendar.DAY_OF_MONTH);//得到天
        map.put("year", year);
        map.put("month", month);
        map.put("day", day);
        return map;
    }
    /**
     * 根据年月日获得date类型时间
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static Date getDateByYearAndMonthAndDay(int year,int month,int day){
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        cal.set(Calendar.HOUR,0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date date = cal.getTime();
        return date;
    }
    /**
     * 获得指定时间 月份最大天数字
     * @param CurrentTime
     * @return
     */
    public static Integer getCurrentMonthMaxDayNumber(Long CurrentTime){
        Calendar calendar = Calendar.getInstance();
        Date currentDate = new Date(CurrentTime);
        calendar.setTime(currentDate);
        int s = Calendar.DAY_OF_MONTH;
        int actualMaximum = calendar.getActualMaximum(s);
        return actualMaximum;
    }
    /**
     * 两个日期相差月数
     * @param date1
     * @param date2
     * @return
     * @throws ParseException
     */
    public static int getMonthSpace(Date date1, Date date2) {
        Calendar bef = Calendar.getInstance();
        Calendar aft = Calendar.getInstance();
        bef.setTime(date1);
        aft.setTime(date2);
        int result = aft.get(Calendar.MONTH) - bef.get(Calendar.MONTH);
        int month = (aft.get(Calendar.YEAR) - bef.get(Calendar.YEAR)) * 12;

        return Math.abs(month + result);
    }
    /**
     * 获得两个时间相差天数
     * @param srcTime
     * @param tarTime
     * @return
     */
    public static Long getRangeDayNumber(Long srcTime,Long tarTime){
        Date srcDate = new Date(srcTime);
        Date tarDate = new Date(tarTime);

        Calendar cal = Calendar.getInstance();
        cal.setTime(srcDate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(tarDate);
        long time2 = cal.getTimeInMillis();
        long between_days=(time2-time1)/(1000*3600*24);
        return between_days;
    }

    //获取当天最大的时间
    public static Date getDayEndTime(){
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        calendar.setTimeInMillis(new Date().getTime());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        calendar.clear();
        calendar.set(year, month, day, 23, 59, 59);
        return calendar.getTime();
    }

    //获取指定时间与当前时间相差秒
    public static long calLastedTime(Date startDate) {
        long a = new Date().getTime();
        long b = startDate.getTime();
        int c = (int)((a - b) / 1000);
        return c*-1;
    }

    public static String getYmd(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(date);
    }

    public static String getMd(int time){
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd");
        return formatter.format(new Date((long)time * 1000));
    }
    public static String getMd2(int time){
        SimpleDateFormat formatter = new SimpleDateFormat("MM.dd");
        return formatter.format(new Date((long) time * 1000));
    }

    public static String toJson(Object obj){
        String json = JSON.toJSONString(obj);
        return json;
    }

    public static int getDateSize(int startTime, int endTime){
        return (endTime - startTime)/ONE_DAY;
    }

    public static String getEndTimeStr(int endTime){
        String endTimeStr = null;
        int currentTime = Utility.getCurrentTimeStamp();
        int dayStartTime = Utility.getDayStartTime(currentTime);
        int dayEndTime = Utility.getDayEndTime(currentTime);
        if(endTime > dayStartTime && endTime <= dayEndTime){
            endTimeStr = Utility.getDataFormatString(currentTime);
        }
        return endTimeStr;
    }

    public static int getMonthStartTime(int timePoint){
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        long theTime = ((long)timePoint)*1000;
        calendar.setTimeInMillis(theTime);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        calendar.clear();
        calendar.set(year, month, 1, 0, 0, 0);
        int ret = (int)(calendar.getTimeInMillis()/1000);
        return ret;
    }

    public static String getDataFormatString(int currentSec, String format) {
        SimpleDateFormat sf = new SimpleDateFormat(format);
        return sf.format(((long)currentSec)*1000);
    }

    public static String formatDouble(double d) {
        return String.format("%.2f", d);
    }

    public static String[] getFormatDoubleToStr(double[] d){
        int size = d.length;
        String[] ss = new String[size];
        for (int i = 0; i < size; i++) {
            ss[i] = SIMPLE_DECIMAL_FORMAT.format(d[i]);
        }
        return ss;
    }

    public static List<String[]> getFormatDoubleListToStr(List<double[]> list){
        List<String[]> result = new ArrayList<>();
        for(double[] d : list){
            String[] ss = getFormatDoubleToStr(d);
            result.add(ss);
        }
        return result;
    }

    public static BigDecimal compareTwoNumber(BigDecimal a, BigDecimal b){
        if(a.compareTo(BigDecimal.ZERO) == 0){
            return new BigDecimal(-2147483648);
        }
        BigDecimal ret;
        if(a.compareTo(b) == 0){
            ret = BigDecimal.ZERO;
        }else if(b.compareTo(BigDecimal.ZERO) == 0){
            ret = new BigDecimal(-100);
        }else if(a.compareTo(BigDecimal.ZERO) == 0){
            ret = BigDecimal.ZERO;
        }else {
            ret = b.subtract(a).multiply(new BigDecimal(100)).divide(a, 2, BigDecimal.ROUND_HALF_EVEN);
        }
        if(b.compareTo(a) < 0){
            ret = ret.abs().multiply(new BigDecimal(-1));
        }else {
            ret = ret.abs();
        }
        return ret;
    }

    public static BigDecimal beatTwoNumber(BigDecimal a, BigDecimal b){
        if(a == null || b == null){
            return BigDecimal.ZERO;
        }
        BigDecimal rank = BigDecimal.ZERO;
        if(a.compareTo(b) == 0){
            rank = new BigDecimal(0);
        }else if(a.compareTo(new BigDecimal(1)) == 0){
            rank = new BigDecimal(100);
        }else if(a.compareTo(BigDecimal.ZERO) > 0 && b.compareTo(BigDecimal.ZERO) > 0){
            rank = b.subtract(a).multiply(new BigDecimal(100)).divide(b, 0, BigDecimal.ROUND_HALF_EVEN);
        }
        return rank;
    }

    public static String formatPercent(Object obj){
        try {
            DecimalFormat df = new DecimalFormat("#0.00");
            return df.format(obj);
        }catch (Exception e){
            e.printStackTrace();
            return "0";
        }

    }

    public static String formatPercent(Object obj,int size){
        try {
            String s = "#0";
            if(size > 0){
                s += ".";
            }
            for(int i = 0;i<size;i++){
                s += "0";
            }
            DecimalFormat df = new DecimalFormat(s);
            return df.format(obj);
        }catch (Exception e){
            e.printStackTrace();
            return "0";
        }

    }

    public static String formatNumber(Object obj){
        if(obj == null){
            return null;
        }
        DecimalFormat df = new DecimalFormat("#0.00");
        String s = df.format(obj);
        return s;
    }

    public static boolean checkIsCache(){
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.HOUR_OF_DAY) >= 9;
    }

    public static boolean checkIsCacheForTask(){
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.HOUR_OF_DAY) > 9;
    }

    public static int getDayRemainTime() {
        int now = getCurrentTimeStamp();
        int datEndTime = getDayEndTime(now);
        int remainTime = datEndTime - now;
        return remainTime;
    }
    public static int getDateIntValue(String time){
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = formatter.parse(time);
            int value = (int)(date.getTime()/1000);
            return value;
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    public static String getUUID() {
        return UUID.randomUUID().toString();
    }

    public static final String getCacheKey(String suffix) {
        if (Utility.isEmpty(suffix)) {
            logger.warn("getCacheKey: suffix is null.");
            return null;
        }
        return "rainbow:cache:" + suffix ;
    }

    public static final String getLockerKey(String suffix) {
        if (Utility.isEmpty(suffix)) {
            logger.warn("getLockerKey: suffix is null.");
            return null;
        }
        return "rainbow:locker:" + suffix;
    }

    public static final int getSubMonthDay(int sub){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, sub);
        calendar.add(Calendar.DAY_OF_MONTH,1);
        int startTime = (int)(calendar.getTimeInMillis()/1000);
        startTime = Utility.getDayStartTime(startTime);
        return startTime;
    }

    /**
     * xiewenda 从一个集合去重掉包含另一个集合的值
     * @param source 源集合
     * @param target 目标集合
     * @return
     */
    public static List<Object> removeAllPlus(List<Object> source,List<Object> target){
        if(source==null || target==null) return source;
        List<Object> sourceList = new ArrayList<>();
        Set<Object> targetSet = new HashSet<>(target);
        for(Object obj : source){
            if(!targetSet.contains(obj)){
                sourceList.add(obj);
            }
        }
        return sourceList;
    }

    public static void main(String[] args) {
           List source = new ArrayList();
           List target = new ArrayList();
           for(int i=0;i<100000;i++){
               String uuid = Utility.getUUID();
               source.add(uuid);
               if(i%10==0){
                   target.add(uuid);
               }
           }

           Long startTime = System.currentTimeMillis();
           System.out.println("source = " + source.size());
           //source.removeAll(target);
           source = Utility.removeAllPlus(source, target);
           System.out.println("source = " + source.size());
           Long endTime = System.currentTimeMillis();
           System.out.println("用时 = "+(endTime-startTime)+"ms");

    }

}
