package common

import java.util.Calendar

/**
 * Created by dykim on 2/17/16.
 */
object Utils {
  //해당주의 월요일 조회
  def getMonday(): java.sql.Date = {
    val cal = Calendar.getInstance()
    cal.setFirstDayOfWeek(Calendar.MONDAY)
    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    val monday = new java.sql.Date(new java.util.Date(cal.getTimeInMillis()).getTime)
    monday
  }

}
