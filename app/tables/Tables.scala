package tables

// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = slick.driver.MySQLDriver
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: slick.driver.JdbcProfile
  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema = Array(Activities.schema, ActivityRecords.schema, ActivityType.schema, AnalysisCategory.schema, CustomActivities.schema, CustomActivityRecords.schema, CustomActivityType.schema, CustomTextbooks.schema, DDay.schema, Departments.schema, EducationalInst.schema, EduInstType.schema, Exam.schema, ExamCriteria.schema, ExamRecords.schema, ExamRecordType.schema, LectureList.schema, LectureRecords.schema, Lectures.schema, Mentors.schema, MentorSubjects.schema, Messages.schema, MSMatching.schema, OptionalSubjects.schema, PostingImages.schema, PostingReport.schema, Postings.schema, PostingType.schema, PushMessage.schema, RecommendUniversity.schema, RefundHistory.schema, Replies.schema, ReplyLike.schema, ReplyReport.schema, SaveHistory.schema, SchoolExam.schema, SchoolExamRecord.schema, SelectedTextbook.schema, Students.schema, Studies.schema, StudyGoal.schema, StudyRecords.schema, StudyTime.schema, StudyTimeSubject.schema, StudyTimeSubSubject.schema, StudyType.schema, Subjects.schema, TargetDepartments.schema, Teachers.schema, Textbooks.schema, TSMatching.schema, UserEdu.schema, Users.schema, UserType.schema).reduceLeft(_ ++ _)
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table Activities
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param createdAt Database column created_at SqlType(DATE), Default(None)
   *  @param goal Database column goal SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param contents Database column contents SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param isRepeated Database column is_repeated SqlType(BIT), Default(None)
   *  @param studentId Database column student_id SqlType(INT)
   *  @param typeId Database column type_id SqlType(INT)
   *  @param isDeleted Database column is_deleted SqlType(BIT), Default(None) */
  case class ActivitiesRow(id: Int, createdAt: Option[java.sql.Date] = None, goal: Option[String] = None, contents: Option[String] = None, isRepeated: Option[Boolean] = None, studentId: Int, typeId: Int, isDeleted: Option[Boolean] = None)
  /** GetResult implicit for fetching ActivitiesRow objects using plain SQL queries */
  implicit def GetResultActivitiesRow(implicit e0: GR[Int], e1: GR[Option[java.sql.Date]], e2: GR[Option[String]], e3: GR[Option[Boolean]]): GR[ActivitiesRow] = GR{
    prs => import prs._
    ActivitiesRow.tupled((<<[Int], <<?[java.sql.Date], <<?[String], <<?[String], <<?[Boolean], <<[Int], <<[Int], <<?[Boolean]))
  }
  /** Table description of table Activities. Objects of this class serve as prototypes for rows in queries. */
  class Activities(_tableTag: Tag) extends Table[ActivitiesRow](_tableTag, "Activities") {
    def * = (id, createdAt, goal, contents, isRepeated, studentId, typeId, isDeleted) <> (ActivitiesRow.tupled, ActivitiesRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), createdAt, goal, contents, isRepeated, Rep.Some(studentId), Rep.Some(typeId), isDeleted).shaped.<>({r=>import r._; _1.map(_=> ActivitiesRow.tupled((_1.get, _2, _3, _4, _5, _6.get, _7.get, _8)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column created_at SqlType(DATE), Default(None) */
    val createdAt: Rep[Option[java.sql.Date]] = column[Option[java.sql.Date]]("created_at", O.Default(None))
    /** Database column goal SqlType(VARCHAR), Length(255,true), Default(None) */
    val goal: Rep[Option[String]] = column[Option[String]]("goal", O.Length(255,varying=true), O.Default(None))
    /** Database column contents SqlType(VARCHAR), Length(255,true), Default(None) */
    val contents: Rep[Option[String]] = column[Option[String]]("contents", O.Length(255,varying=true), O.Default(None))
    /** Database column is_repeated SqlType(BIT), Default(None) */
    val isRepeated: Rep[Option[Boolean]] = column[Option[Boolean]]("is_repeated", O.Default(None))
    /** Database column student_id SqlType(INT) */
    val studentId: Rep[Int] = column[Int]("student_id")
    /** Database column type_id SqlType(INT) */
    val typeId: Rep[Int] = column[Int]("type_id")
    /** Database column is_deleted SqlType(BIT), Default(None) */
    val isDeleted: Rep[Option[Boolean]] = column[Option[Boolean]]("is_deleted", O.Default(None))

    /** Foreign key referencing ActivityType (database name Activities_ibfk_1) */
    lazy val activityTypeFk1 = foreignKey("Activities_ibfk_1", typeId, ActivityType)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing ActivityType (database name Activities_ibfk_3) */
    lazy val activityTypeFk2 = foreignKey("Activities_ibfk_3", typeId, ActivityType)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Students (database name Activities_ibfk_2) */
    lazy val studentsFk = foreignKey("Activities_ibfk_2", studentId, Students)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table Activities */
  lazy val Activities = new TableQuery(tag => new Activities(tag))

  /** Entity class storing rows of table ActivityRecords
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param createdAt Database column created_at SqlType(DATETIME), Default(None)
   *  @param startTime Database column start_time SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param endTime Database column end_time SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param duration Database column duration SqlType(BIGINT), Default(None)
   *  @param activityId Database column activity_id SqlType(INT) */
  case class ActivityRecordsRow(id: Int, createdAt: Option[java.sql.Timestamp] = None, startTime: Option[String] = None, endTime: Option[String] = None, duration: Option[Long] = None, activityId: Int)
  /** GetResult implicit for fetching ActivityRecordsRow objects using plain SQL queries */
  implicit def GetResultActivityRecordsRow(implicit e0: GR[Int], e1: GR[Option[java.sql.Timestamp]], e2: GR[Option[String]], e3: GR[Option[Long]]): GR[ActivityRecordsRow] = GR{
    prs => import prs._
    ActivityRecordsRow.tupled((<<[Int], <<?[java.sql.Timestamp], <<?[String], <<?[String], <<?[Long], <<[Int]))
  }
  /** Table description of table Activity_records. Objects of this class serve as prototypes for rows in queries. */
  class ActivityRecords(_tableTag: Tag) extends Table[ActivityRecordsRow](_tableTag, "Activity_records") {
    def * = (id, createdAt, startTime, endTime, duration, activityId) <> (ActivityRecordsRow.tupled, ActivityRecordsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), createdAt, startTime, endTime, duration, Rep.Some(activityId)).shaped.<>({r=>import r._; _1.map(_=> ActivityRecordsRow.tupled((_1.get, _2, _3, _4, _5, _6.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column created_at SqlType(DATETIME), Default(None) */
    val createdAt: Rep[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("created_at", O.Default(None))
    /** Database column start_time SqlType(VARCHAR), Length(255,true), Default(None) */
    val startTime: Rep[Option[String]] = column[Option[String]]("start_time", O.Length(255,varying=true), O.Default(None))
    /** Database column end_time SqlType(VARCHAR), Length(255,true), Default(None) */
    val endTime: Rep[Option[String]] = column[Option[String]]("end_time", O.Length(255,varying=true), O.Default(None))
    /** Database column duration SqlType(BIGINT), Default(None) */
    val duration: Rep[Option[Long]] = column[Option[Long]]("duration", O.Default(None))
    /** Database column activity_id SqlType(INT) */
    val activityId: Rep[Int] = column[Int]("activity_id")

    /** Foreign key referencing Activities (database name Activity_records_ibfk_1) */
    lazy val activitiesFk = foreignKey("Activity_records_ibfk_1", activityId, Activities)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table ActivityRecords */
  lazy val ActivityRecords = new TableQuery(tag => new ActivityRecords(tag))

  /** Entity class storing rows of table ActivityType
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param title Database column title SqlType(TEXT), Default(None)
   *  @param isStudy Database column is_study SqlType(BIT), Default(None)
   *  @param isAlone Database column is_alone SqlType(BIT), Default(None) */
  case class ActivityTypeRow(id: Int, title: Option[String] = None, isStudy: Option[Boolean] = None, isAlone: Option[Boolean] = None)
  /** GetResult implicit for fetching ActivityTypeRow objects using plain SQL queries */
  implicit def GetResultActivityTypeRow(implicit e0: GR[Int], e1: GR[Option[String]], e2: GR[Option[Boolean]]): GR[ActivityTypeRow] = GR{
    prs => import prs._
    ActivityTypeRow.tupled((<<[Int], <<?[String], <<?[Boolean], <<?[Boolean]))
  }
  /** Table description of table Activity_type. Objects of this class serve as prototypes for rows in queries. */
  class ActivityType(_tableTag: Tag) extends Table[ActivityTypeRow](_tableTag, "Activity_type") {
    def * = (id, title, isStudy, isAlone) <> (ActivityTypeRow.tupled, ActivityTypeRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), title, isStudy, isAlone).shaped.<>({r=>import r._; _1.map(_=> ActivityTypeRow.tupled((_1.get, _2, _3, _4)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column title SqlType(TEXT), Default(None) */
    val title: Rep[Option[String]] = column[Option[String]]("title", O.Default(None))
    /** Database column is_study SqlType(BIT), Default(None) */
    val isStudy: Rep[Option[Boolean]] = column[Option[Boolean]]("is_study", O.Default(None))
    /** Database column is_alone SqlType(BIT), Default(None) */
    val isAlone: Rep[Option[Boolean]] = column[Option[Boolean]]("is_alone", O.Default(None))
  }
  /** Collection-like TableQuery object for table ActivityType */
  lazy val ActivityType = new TableQuery(tag => new ActivityType(tag))

  /** Entity class storing rows of table AnalysisCategory
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param title Database column title SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param depth1 Database column depth1 SqlType(INT), Default(None)
   *  @param depth2 Database column depth2 SqlType(INT), Default(None) */
  case class AnalysisCategoryRow(id: Int, title: Option[String] = None, depth1: Option[Int] = None, depth2: Option[Int] = None)
  /** GetResult implicit for fetching AnalysisCategoryRow objects using plain SQL queries */
  implicit def GetResultAnalysisCategoryRow(implicit e0: GR[Int], e1: GR[Option[String]], e2: GR[Option[Int]]): GR[AnalysisCategoryRow] = GR{
    prs => import prs._
    AnalysisCategoryRow.tupled((<<[Int], <<?[String], <<?[Int], <<?[Int]))
  }
  /** Table description of table Analysis_category. Objects of this class serve as prototypes for rows in queries. */
  class AnalysisCategory(_tableTag: Tag) extends Table[AnalysisCategoryRow](_tableTag, "Analysis_category") {
    def * = (id, title, depth1, depth2) <> (AnalysisCategoryRow.tupled, AnalysisCategoryRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), title, depth1, depth2).shaped.<>({r=>import r._; _1.map(_=> AnalysisCategoryRow.tupled((_1.get, _2, _3, _4)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column title SqlType(VARCHAR), Length(255,true), Default(None) */
    val title: Rep[Option[String]] = column[Option[String]]("title", O.Length(255,varying=true), O.Default(None))
    /** Database column depth1 SqlType(INT), Default(None) */
    val depth1: Rep[Option[Int]] = column[Option[Int]]("depth1", O.Default(None))
    /** Database column depth2 SqlType(INT), Default(None) */
    val depth2: Rep[Option[Int]] = column[Option[Int]]("depth2", O.Default(None))
  }
  /** Collection-like TableQuery object for table AnalysisCategory */
  lazy val AnalysisCategory = new TableQuery(tag => new AnalysisCategory(tag))

  /** Entity class storing rows of table ChangeChoiceHistory
    *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
    *  @param postingId Database column posting_id SqlType(INT), Default(None)
    *  @param choicedId Database column choiced_id SqlType(INT), Default(None)
    *  @param newAnswerId Database column new_answer_id SqlType(INT), Default(None)
    *  @param isChanged Database column is_changed SqlType(BIT), Default(false)
    *  @param isCanceled Database column is_canceled SqlType(BIT), Default(false) */
  case class ChangeChoiceHistoryRow(id: Int, postingId: Option[Int] = None, choicedId: Option[Int] = None, newAnswerId: Option[Int] = None, isChanged: Boolean = false, isCanceled: Boolean = false)
  /** GetResult implicit for fetching ChangeChoiceHistoryRow objects using plain SQL queries */
  implicit def GetResultChangeChoiceHistoryRow(implicit e0: GR[Int], e1: GR[Option[Int]], e2: GR[Boolean]): GR[ChangeChoiceHistoryRow] = GR{
    prs => import prs._
      ChangeChoiceHistoryRow.tupled((<<[Int], <<?[Int], <<?[Int], <<?[Int], <<[Boolean], <<[Boolean]))
  }
  /** Table description of table Change_choice_history. Objects of this class serve as prototypes for rows in queries. */
  class ChangeChoiceHistory(_tableTag: Tag) extends Table[ChangeChoiceHistoryRow](_tableTag, "Change_choice_history") {
    def * = (id, postingId, choicedId, newAnswerId, isChanged, isCanceled) <> (ChangeChoiceHistoryRow.tupled, ChangeChoiceHistoryRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), postingId, choicedId, newAnswerId, Rep.Some(isChanged), Rep.Some(isCanceled)).shaped.<>({r=>import r._; _1.map(_=> ChangeChoiceHistoryRow.tupled((_1.get, _2, _3, _4, _5.get, _6.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column posting_id SqlType(INT), Default(None) */
    val postingId: Rep[Option[Int]] = column[Option[Int]]("posting_id", O.Default(None))
    /** Database column choiced_id SqlType(INT), Default(None) */
    val choicedId: Rep[Option[Int]] = column[Option[Int]]("choiced_id", O.Default(None))
    /** Database column new_answer_id SqlType(INT), Default(None) */
    val newAnswerId: Rep[Option[Int]] = column[Option[Int]]("new_answer_id", O.Default(None))
    /** Database column is_changed SqlType(BIT), Default(false) */
    val isChanged: Rep[Boolean] = column[Boolean]("is_changed", O.Default(false))
    /** Database column is_canceled SqlType(BIT), Default(false) */
    val isCanceled: Rep[Boolean] = column[Boolean]("is_canceled", O.Default(false))
  }
  /** Collection-like TableQuery object for table ChangeChoiceHistory */
  lazy val ChangeChoiceHistory = new TableQuery(tag => new ChangeChoiceHistory(tag))

  /** Entity class storing rows of table CustomActivities
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param createdAt Database column created_at SqlType(DATE), Default(None)
   *  @param goal Database column goal SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param contents Database column contents SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param isRepeated Database column is_repeated SqlType(BIT), Default(None)
   *  @param isDeleted Database column is_deleted SqlType(BIT), Default(None)
   *  @param studentId Database column student_id SqlType(INT)
   *  @param customActivityTypeId Database column custom_activity_type_id SqlType(INT) */
  case class CustomActivitiesRow(id: Int, createdAt: Option[java.sql.Date] = None, goal: Option[String] = None, contents: Option[String] = None, isRepeated: Option[Boolean] = None, isDeleted: Option[Boolean] = None, studentId: Int, customActivityTypeId: Int)
  /** GetResult implicit for fetching CustomActivitiesRow objects using plain SQL queries */
  implicit def GetResultCustomActivitiesRow(implicit e0: GR[Int], e1: GR[Option[java.sql.Date]], e2: GR[Option[String]], e3: GR[Option[Boolean]]): GR[CustomActivitiesRow] = GR{
    prs => import prs._
    CustomActivitiesRow.tupled((<<[Int], <<?[java.sql.Date], <<?[String], <<?[String], <<?[Boolean], <<?[Boolean], <<[Int], <<[Int]))
  }
  /** Table description of table Custom_activities. Objects of this class serve as prototypes for rows in queries. */
  class CustomActivities(_tableTag: Tag) extends Table[CustomActivitiesRow](_tableTag, "Custom_activities") {
    def * = (id, createdAt, goal, contents, isRepeated, isDeleted, studentId, customActivityTypeId) <> (CustomActivitiesRow.tupled, CustomActivitiesRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), createdAt, goal, contents, isRepeated, isDeleted, Rep.Some(studentId), Rep.Some(customActivityTypeId)).shaped.<>({r=>import r._; _1.map(_=> CustomActivitiesRow.tupled((_1.get, _2, _3, _4, _5, _6, _7.get, _8.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column created_at SqlType(DATE), Default(None) */
    val createdAt: Rep[Option[java.sql.Date]] = column[Option[java.sql.Date]]("created_at", O.Default(None))
    /** Database column goal SqlType(VARCHAR), Length(255,true), Default(None) */
    val goal: Rep[Option[String]] = column[Option[String]]("goal", O.Length(255,varying=true), O.Default(None))
    /** Database column contents SqlType(VARCHAR), Length(255,true), Default(None) */
    val contents: Rep[Option[String]] = column[Option[String]]("contents", O.Length(255,varying=true), O.Default(None))
    /** Database column is_repeated SqlType(BIT), Default(None) */
    val isRepeated: Rep[Option[Boolean]] = column[Option[Boolean]]("is_repeated", O.Default(None))
    /** Database column is_deleted SqlType(BIT), Default(None) */
    val isDeleted: Rep[Option[Boolean]] = column[Option[Boolean]]("is_deleted", O.Default(None))
    /** Database column student_id SqlType(INT) */
    val studentId: Rep[Int] = column[Int]("student_id")
    /** Database column custom_activity_type_id SqlType(INT) */
    val customActivityTypeId: Rep[Int] = column[Int]("custom_activity_type_id")

    /** Foreign key referencing CustomActivityType (database name Custom_activities_ibfk_1) */
    lazy val customActivityTypeFk = foreignKey("Custom_activities_ibfk_1", customActivityTypeId, CustomActivityType)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Students (database name Custom_activities_ibfk_2) */
    lazy val studentsFk = foreignKey("Custom_activities_ibfk_2", studentId, Students)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table CustomActivities */
  lazy val CustomActivities = new TableQuery(tag => new CustomActivities(tag))

  /** Entity class storing rows of table CustomActivityRecords
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param createdAt Database column created_at SqlType(DATETIME), Default(None)
   *  @param startTime Database column start_time SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param endTime Database column end_time SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param duration Database column duration SqlType(BIGINT), Default(None)
   *  @param customActivityId Database column custom_activity_id SqlType(INT) */
  case class CustomActivityRecordsRow(id: Int, createdAt: Option[java.sql.Timestamp] = None, startTime: Option[String] = None, endTime: Option[String] = None, duration: Option[Long] = None, customActivityId: Int)
  /** GetResult implicit for fetching CustomActivityRecordsRow objects using plain SQL queries */
  implicit def GetResultCustomActivityRecordsRow(implicit e0: GR[Int], e1: GR[Option[java.sql.Timestamp]], e2: GR[Option[String]], e3: GR[Option[Long]]): GR[CustomActivityRecordsRow] = GR{
    prs => import prs._
    CustomActivityRecordsRow.tupled((<<[Int], <<?[java.sql.Timestamp], <<?[String], <<?[String], <<?[Long], <<[Int]))
  }
  /** Table description of table Custom_activity_records. Objects of this class serve as prototypes for rows in queries. */
  class CustomActivityRecords(_tableTag: Tag) extends Table[CustomActivityRecordsRow](_tableTag, "Custom_activity_records") {
    def * = (id, createdAt, startTime, endTime, duration, customActivityId) <> (CustomActivityRecordsRow.tupled, CustomActivityRecordsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), createdAt, startTime, endTime, duration, Rep.Some(customActivityId)).shaped.<>({r=>import r._; _1.map(_=> CustomActivityRecordsRow.tupled((_1.get, _2, _3, _4, _5, _6.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column created_at SqlType(DATETIME), Default(None) */
    val createdAt: Rep[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("created_at", O.Default(None))
    /** Database column start_time SqlType(VARCHAR), Length(255,true), Default(None) */
    val startTime: Rep[Option[String]] = column[Option[String]]("start_time", O.Length(255,varying=true), O.Default(None))
    /** Database column end_time SqlType(VARCHAR), Length(255,true), Default(None) */
    val endTime: Rep[Option[String]] = column[Option[String]]("end_time", O.Length(255,varying=true), O.Default(None))
    /** Database column duration SqlType(BIGINT), Default(None) */
    val duration: Rep[Option[Long]] = column[Option[Long]]("duration", O.Default(None))
    /** Database column custom_activity_id SqlType(INT) */
    val customActivityId: Rep[Int] = column[Int]("custom_activity_id")

    /** Foreign key referencing CustomActivities (database name Custom_activity_records_ibfk_1) */
    lazy val customActivitiesFk = foreignKey("Custom_activity_records_ibfk_1", customActivityId, CustomActivities)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table CustomActivityRecords */
  lazy val CustomActivityRecords = new TableQuery(tag => new CustomActivityRecords(tag))

  /** Entity class storing rows of table CustomActivityType
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param title Database column title SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param iconId Database column icon_id SqlType(INT), Default(None)
   *  @param studentId Database column student_id SqlType(INT)
   *  @param isDelete Database column is_delete SqlType(BIT), Default(None) */
  case class CustomActivityTypeRow(id: Int, title: Option[String] = None, iconId: Option[Int] = None, studentId: Int, isDelete: Option[Boolean] = None)
  /** GetResult implicit for fetching CustomActivityTypeRow objects using plain SQL queries */
  implicit def GetResultCustomActivityTypeRow(implicit e0: GR[Int], e1: GR[Option[String]], e2: GR[Option[Int]], e3: GR[Option[Boolean]]): GR[CustomActivityTypeRow] = GR{
    prs => import prs._
    CustomActivityTypeRow.tupled((<<[Int], <<?[String], <<?[Int], <<[Int], <<?[Boolean]))
  }
  /** Table description of table Custom_activity_type. Objects of this class serve as prototypes for rows in queries. */
  class CustomActivityType(_tableTag: Tag) extends Table[CustomActivityTypeRow](_tableTag, "Custom_activity_type") {
    def * = (id, title, iconId, studentId, isDelete) <> (CustomActivityTypeRow.tupled, CustomActivityTypeRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), title, iconId, Rep.Some(studentId), isDelete).shaped.<>({r=>import r._; _1.map(_=> CustomActivityTypeRow.tupled((_1.get, _2, _3, _4.get, _5)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column title SqlType(VARCHAR), Length(255,true), Default(None) */
    val title: Rep[Option[String]] = column[Option[String]]("title", O.Length(255,varying=true), O.Default(None))
    /** Database column icon_id SqlType(INT), Default(None) */
    val iconId: Rep[Option[Int]] = column[Option[Int]]("icon_id", O.Default(None))
    /** Database column student_id SqlType(INT) */
    val studentId: Rep[Int] = column[Int]("student_id")
    /** Database column is_delete SqlType(BIT), Default(None) */
    val isDelete: Rep[Option[Boolean]] = column[Option[Boolean]]("is_delete", O.Default(None))

    /** Foreign key referencing Students (database name Custom_activity_type_ibfk_1) */
    lazy val studentsFk = foreignKey("Custom_activity_type_ibfk_1", studentId, Students)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table CustomActivityType */
  lazy val CustomActivityType = new TableQuery(tag => new CustomActivityType(tag))

  /** Entity class storing rows of table CustomTextbooks
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param title Database column title SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param subjectId Database column subject_id SqlType(INT), Default(None)
   *  @param userId Database column user_id SqlType(INT), Default(None) */
  case class CustomTextbooksRow(id: Int, title: Option[String] = None, subjectId: Option[Int] = None, userId: Option[Int] = None)
  /** GetResult implicit for fetching CustomTextbooksRow objects using plain SQL queries */
  implicit def GetResultCustomTextbooksRow(implicit e0: GR[Int], e1: GR[Option[String]], e2: GR[Option[Int]]): GR[CustomTextbooksRow] = GR{
    prs => import prs._
    CustomTextbooksRow.tupled((<<[Int], <<?[String], <<?[Int], <<?[Int]))
  }
  /** Table description of table Custom_textbooks. Objects of this class serve as prototypes for rows in queries. */
  class CustomTextbooks(_tableTag: Tag) extends Table[CustomTextbooksRow](_tableTag, "Custom_textbooks") {
    def * = (id, title, subjectId, userId) <> (CustomTextbooksRow.tupled, CustomTextbooksRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), title, subjectId, userId).shaped.<>({r=>import r._; _1.map(_=> CustomTextbooksRow.tupled((_1.get, _2, _3, _4)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column title SqlType(VARCHAR), Length(255,true), Default(None) */
    val title: Rep[Option[String]] = column[Option[String]]("title", O.Length(255,varying=true), O.Default(None))
    /** Database column subject_id SqlType(INT), Default(None) */
    val subjectId: Rep[Option[Int]] = column[Option[Int]]("subject_id", O.Default(None))
    /** Database column user_id SqlType(INT), Default(None) */
    val userId: Rep[Option[Int]] = column[Option[Int]]("user_id", O.Default(None))
  }
  /** Collection-like TableQuery object for table CustomTextbooks */
  lazy val CustomTextbooks = new TableQuery(tag => new CustomTextbooks(tag))

  /** Entity class storing rows of table DDay
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param title Database column title SqlType(TEXT), Default(None)
   *  @param date Database column date SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param isActive Database column is_active SqlType(BIT), Default(None)
   *  @param studentId Database column student_id SqlType(INT) */
  case class DDayRow(id: Int, title: Option[String] = None, date: Option[String] = None, isActive: Option[Boolean] = None, studentId: Int)
  /** GetResult implicit for fetching DDayRow objects using plain SQL queries */
  implicit def GetResultDDayRow(implicit e0: GR[Int], e1: GR[Option[String]], e2: GR[Option[Boolean]]): GR[DDayRow] = GR{
    prs => import prs._
    DDayRow.tupled((<<[Int], <<?[String], <<?[String], <<?[Boolean], <<[Int]))
  }
  /** Table description of table D_day. Objects of this class serve as prototypes for rows in queries. */
  class DDay(_tableTag: Tag) extends Table[DDayRow](_tableTag, "D_day") {
    def * = (id, title, date, isActive, studentId) <> (DDayRow.tupled, DDayRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), title, date, isActive, Rep.Some(studentId)).shaped.<>({r=>import r._; _1.map(_=> DDayRow.tupled((_1.get, _2, _3, _4, _5.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column title SqlType(TEXT), Default(None) */
    val title: Rep[Option[String]] = column[Option[String]]("title", O.Default(None))
    /** Database column date SqlType(VARCHAR), Length(255,true), Default(None) */
    val date: Rep[Option[String]] = column[Option[String]]("date", O.Length(255,varying=true), O.Default(None))
    /** Database column is_active SqlType(BIT), Default(None) */
    val isActive: Rep[Option[Boolean]] = column[Option[Boolean]]("is_active", O.Default(None))
    /** Database column student_id SqlType(INT) */
    val studentId: Rep[Int] = column[Int]("student_id")

    /** Foreign key referencing Students (database name D_day_ibfk_1) */
    lazy val studentsFk = foreignKey("D_day_ibfk_1", studentId, Students)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table DDay */
  lazy val DDay = new TableQuery(tag => new DDay(tag))

  /** Entity class storing rows of table Departments
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param title Database column title SqlType(TEXT), Default(None)
   *  @param depth1 Database column depth1 SqlType(INT), Default(None) */
  case class DepartmentsRow(id: Int, title: Option[String] = None, depth1: Option[Int] = None)
  /** GetResult implicit for fetching DepartmentsRow objects using plain SQL queries */
  implicit def GetResultDepartmentsRow(implicit e0: GR[Int], e1: GR[Option[String]], e2: GR[Option[Int]]): GR[DepartmentsRow] = GR{
    prs => import prs._
    DepartmentsRow.tupled((<<[Int], <<?[String], <<?[Int]))
  }
  /** Table description of table Departments. Objects of this class serve as prototypes for rows in queries. */
  class Departments(_tableTag: Tag) extends Table[DepartmentsRow](_tableTag, "Departments") {
    def * = (id, title, depth1) <> (DepartmentsRow.tupled, DepartmentsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), title, depth1).shaped.<>({r=>import r._; _1.map(_=> DepartmentsRow.tupled((_1.get, _2, _3)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column title SqlType(TEXT), Default(None) */
    val title: Rep[Option[String]] = column[Option[String]]("title", O.Default(None))
    /** Database column depth1 SqlType(INT), Default(None) */
    val depth1: Rep[Option[Int]] = column[Option[Int]]("depth1", O.Default(None))
  }
  /** Collection-like TableQuery object for table Departments */
  lazy val Departments = new TableQuery(tag => new Departments(tag))

  /** Entity class storing rows of table EducationalInst
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param name Database column name SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param location Database column location SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param typeId Database column type_id SqlType(INT)
   *  @param zipcode Database column zipcode SqlType(VARCHAR), Length(255,true), Default(None) */
  case class EducationalInstRow(id: Int, name: Option[String] = None, location: Option[String] = None, typeId: Int, zipcode: Option[String] = None)
  /** GetResult implicit for fetching EducationalInstRow objects using plain SQL queries */
  implicit def GetResultEducationalInstRow(implicit e0: GR[Int], e1: GR[Option[String]]): GR[EducationalInstRow] = GR{
    prs => import prs._
    EducationalInstRow.tupled((<<[Int], <<?[String], <<?[String], <<[Int], <<?[String]))
  }
  /** Table description of table Educational_inst. Objects of this class serve as prototypes for rows in queries. */
  class EducationalInst(_tableTag: Tag) extends Table[EducationalInstRow](_tableTag, "Educational_inst") {
    def * = (id, name, location, typeId, zipcode) <> (EducationalInstRow.tupled, EducationalInstRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), name, location, Rep.Some(typeId), zipcode).shaped.<>({r=>import r._; _1.map(_=> EducationalInstRow.tupled((_1.get, _2, _3, _4.get, _5)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column name SqlType(VARCHAR), Length(255,true), Default(None) */
    val name: Rep[Option[String]] = column[Option[String]]("name", O.Length(255,varying=true), O.Default(None))
    /** Database column location SqlType(VARCHAR), Length(255,true), Default(None) */
    val location: Rep[Option[String]] = column[Option[String]]("location", O.Length(255,varying=true), O.Default(None))
    /** Database column type_id SqlType(INT) */
    val typeId: Rep[Int] = column[Int]("type_id")
    /** Database column zipcode SqlType(VARCHAR), Length(255,true), Default(None) */
    val zipcode: Rep[Option[String]] = column[Option[String]]("zipcode", O.Length(255,varying=true), O.Default(None))

    /** Foreign key referencing EduInstType (database name Educational_inst_ibfk_1) */
    lazy val eduInstTypeFk = foreignKey("Educational_inst_ibfk_1", typeId, EduInstType)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table EducationalInst */
  lazy val EducationalInst = new TableQuery(tag => new EducationalInst(tag))

  /** Entity class storing rows of table EduInstType
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param `type` Database column type SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param depth1 Database column depth1 SqlType(INT), Default(None)
   *  @param depth2 Database column depth2 SqlType(INT), Default(None)
   *  @param depth3 Database column depth3 SqlType(INT), Default(None) */
  case class EduInstTypeRow(id: Int, `type`: Option[String] = None, depth1: Option[Int] = None, depth2: Option[Int] = None, depth3: Option[Int] = None)
  /** GetResult implicit for fetching EduInstTypeRow objects using plain SQL queries */
  implicit def GetResultEduInstTypeRow(implicit e0: GR[Int], e1: GR[Option[String]], e2: GR[Option[Int]]): GR[EduInstTypeRow] = GR{
    prs => import prs._
    EduInstTypeRow.tupled((<<[Int], <<?[String], <<?[Int], <<?[Int], <<?[Int]))
  }
  /** Table description of table Edu_inst_type. Objects of this class serve as prototypes for rows in queries.
   *  NOTE: The following names collided with Scala keywords and were escaped: type */
  class EduInstType(_tableTag: Tag) extends Table[EduInstTypeRow](_tableTag, "Edu_inst_type") {
    def * = (id, `type`, depth1, depth2, depth3) <> (EduInstTypeRow.tupled, EduInstTypeRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), `type`, depth1, depth2, depth3).shaped.<>({r=>import r._; _1.map(_=> EduInstTypeRow.tupled((_1.get, _2, _3, _4, _5)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column type SqlType(VARCHAR), Length(255,true), Default(None)
     *  NOTE: The name was escaped because it collided with a Scala keyword. */
    val `type`: Rep[Option[String]] = column[Option[String]]("type", O.Length(255,varying=true), O.Default(None))
    /** Database column depth1 SqlType(INT), Default(None) */
    val depth1: Rep[Option[Int]] = column[Option[Int]]("depth1", O.Default(None))
    /** Database column depth2 SqlType(INT), Default(None) */
    val depth2: Rep[Option[Int]] = column[Option[Int]]("depth2", O.Default(None))
    /** Database column depth3 SqlType(INT), Default(None) */
    val depth3: Rep[Option[Int]] = column[Option[Int]]("depth3", O.Default(None))
  }
  /** Collection-like TableQuery object for table EduInstType */
  lazy val EduInstType = new TableQuery(tag => new EduInstType(tag))

  /** Entity class storing rows of table Exam
    *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
    *  @param title Database column title SqlType(TEXT), Default(None)
    *  @param year Database column year SqlType(INT), Default(None)
    *  @param month Database column month SqlType(INT), Default(None)
    *  @param isActive Database column is_active SqlType(BIT), Default(None)
    *  @param isDelete Database column is_delete SqlType(BIT), Default(None) */
  case class ExamRow(id: Int, title: Option[String] = None, year: Option[Int] = None, month: Option[Int] = None, isActive: Option[Boolean] = None, isDelete: Option[Boolean] = None)
  /** GetResult implicit for fetching ExamRow objects using plain SQL queries */
  implicit def GetResultExamRow(implicit e0: GR[Int], e1: GR[Option[String]], e2: GR[Option[Int]], e3: GR[Option[Boolean]]): GR[ExamRow] = GR{
    prs => import prs._
      ExamRow.tupled((<<[Int], <<?[String], <<?[Int], <<?[Int], <<?[Boolean], <<?[Boolean]))
  }
  /** Table description of table Exam. Objects of this class serve as prototypes for rows in queries. */
  class Exam(_tableTag: Tag) extends Table[ExamRow](_tableTag, "Exam") {
    def * = (id, title, year, month, isActive, isDelete) <> (ExamRow.tupled, ExamRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), title, year, month, isActive, isDelete).shaped.<>({r=>import r._; _1.map(_=> ExamRow.tupled((_1.get, _2, _3, _4, _5, _6)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column title SqlType(TEXT), Default(None) */
    val title: Rep[Option[String]] = column[Option[String]]("title", O.Default(None))
    /** Database column year SqlType(INT), Default(None) */
    val year: Rep[Option[Int]] = column[Option[Int]]("year", O.Default(None))
    /** Database column month SqlType(INT), Default(None) */
    val month: Rep[Option[Int]] = column[Option[Int]]("month", O.Default(None))
    /** Database column is_active SqlType(BIT), Default(None) */
    val isActive: Rep[Option[Boolean]] = column[Option[Boolean]]("is_active", O.Default(None))
    /** Database column is_delete SqlType(BIT), Default(None) */
    val isDelete: Rep[Option[Boolean]] = column[Option[Boolean]]("is_delete", O.Default(None))
  }
  /** Collection-like TableQuery object for table Exam */
  lazy val Exam = new TableQuery(tag => new Exam(tag))

  /** Entity class storing rows of table ExamCriteria
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param tier1Min Database column tier1_min SqlType(INT), Default(None)
   *  @param tier1Max Database column tier1_max SqlType(INT), Default(None)
   *  @param tier2Min Database column tier2_min SqlType(INT), Default(None)
   *  @param tier2Max Database column tier2_max SqlType(INT), Default(None)
   *  @param tier3Min Database column tier3_min SqlType(INT), Default(None)
   *  @param tier3Max Database column tier3_max SqlType(INT), Default(None)
   *  @param tier4Min Database column tier4_min SqlType(INT), Default(None)
   *  @param tier4Max Database column tier4_max SqlType(INT), Default(None)
   *  @param tier5Min Database column tier5_min SqlType(INT), Default(None)
   *  @param tier5Max Database column tier5_max SqlType(INT), Default(None)
   *  @param tier6Min Database column tier6_min SqlType(INT), Default(None)
   *  @param tier6Max Database column tier6_max SqlType(INT), Default(None)
   *  @param tier7Min Database column tier7_min SqlType(INT), Default(None)
   *  @param tier7Max Database column tier7_max SqlType(INT), Default(None)
   *  @param tier8Min Database column tier8_min SqlType(INT), Default(None)
   *  @param tier8Max Database column tier8_max SqlType(INT), Default(None)
   *  @param tier9Min Database column tier9_min SqlType(INT), Default(None)
   *  @param tier9Max Database column tier9_max SqlType(INT), Default(None)
   *  @param examId Database column exam_id SqlType(INT)
   *  @param subjectId Database column subject_id SqlType(INT)
   *  @param examRecordTypeId Database column exam_record_type_id SqlType(INT) */
  case class ExamCriteriaRow(id: Int, tier1Min: Option[Int] = None, tier1Max: Option[Int] = None, tier2Min: Option[Int] = None, tier2Max: Option[Int] = None, tier3Min: Option[Int] = None, tier3Max: Option[Int] = None, tier4Min: Option[Int] = None, tier4Max: Option[Int] = None, tier5Min: Option[Int] = None, tier5Max: Option[Int] = None, tier6Min: Option[Int] = None, tier6Max: Option[Int] = None, tier7Min: Option[Int] = None, tier7Max: Option[Int] = None, tier8Min: Option[Int] = None, tier8Max: Option[Int] = None, tier9Min: Option[Int] = None, tier9Max: Option[Int] = None, examId: Int, subjectId: Int, examRecordTypeId: Int)
  /** GetResult implicit for fetching ExamCriteriaRow objects using plain SQL queries */
  implicit def GetResultExamCriteriaRow(implicit e0: GR[Int], e1: GR[Option[Int]]): GR[ExamCriteriaRow] = GR{
    prs => import prs._
    ExamCriteriaRow.tupled((<<[Int], <<?[Int], <<?[Int], <<?[Int], <<?[Int], <<?[Int], <<?[Int], <<?[Int], <<?[Int], <<?[Int], <<?[Int], <<?[Int], <<?[Int], <<?[Int], <<?[Int], <<?[Int], <<?[Int], <<?[Int], <<?[Int], <<[Int], <<[Int], <<[Int]))
  }
  /** Table description of table Exam_criteria. Objects of this class serve as prototypes for rows in queries. */
  class ExamCriteria(_tableTag: Tag) extends Table[ExamCriteriaRow](_tableTag, "Exam_criteria") {
    def * = (id, tier1Min, tier1Max, tier2Min, tier2Max, tier3Min, tier3Max, tier4Min, tier4Max, tier5Min, tier5Max, tier6Min, tier6Max, tier7Min, tier7Max, tier8Min, tier8Max, tier9Min, tier9Max, examId, subjectId, examRecordTypeId) <> (ExamCriteriaRow.tupled, ExamCriteriaRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), tier1Min, tier1Max, tier2Min, tier2Max, tier3Min, tier3Max, tier4Min, tier4Max, tier5Min, tier5Max, tier6Min, tier6Max, tier7Min, tier7Max, tier8Min, tier8Max, tier9Min, tier9Max, Rep.Some(examId), Rep.Some(subjectId), Rep.Some(examRecordTypeId)).shaped.<>({r=>import r._; _1.map(_=> ExamCriteriaRow.tupled((_1.get, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20.get, _21.get, _22.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column tier1_min SqlType(INT), Default(None) */
    val tier1Min: Rep[Option[Int]] = column[Option[Int]]("tier1_min", O.Default(None))
    /** Database column tier1_max SqlType(INT), Default(None) */
    val tier1Max: Rep[Option[Int]] = column[Option[Int]]("tier1_max", O.Default(None))
    /** Database column tier2_min SqlType(INT), Default(None) */
    val tier2Min: Rep[Option[Int]] = column[Option[Int]]("tier2_min", O.Default(None))
    /** Database column tier2_max SqlType(INT), Default(None) */
    val tier2Max: Rep[Option[Int]] = column[Option[Int]]("tier2_max", O.Default(None))
    /** Database column tier3_min SqlType(INT), Default(None) */
    val tier3Min: Rep[Option[Int]] = column[Option[Int]]("tier3_min", O.Default(None))
    /** Database column tier3_max SqlType(INT), Default(None) */
    val tier3Max: Rep[Option[Int]] = column[Option[Int]]("tier3_max", O.Default(None))
    /** Database column tier4_min SqlType(INT), Default(None) */
    val tier4Min: Rep[Option[Int]] = column[Option[Int]]("tier4_min", O.Default(None))
    /** Database column tier4_max SqlType(INT), Default(None) */
    val tier4Max: Rep[Option[Int]] = column[Option[Int]]("tier4_max", O.Default(None))
    /** Database column tier5_min SqlType(INT), Default(None) */
    val tier5Min: Rep[Option[Int]] = column[Option[Int]]("tier5_min", O.Default(None))
    /** Database column tier5_max SqlType(INT), Default(None) */
    val tier5Max: Rep[Option[Int]] = column[Option[Int]]("tier5_max", O.Default(None))
    /** Database column tier6_min SqlType(INT), Default(None) */
    val tier6Min: Rep[Option[Int]] = column[Option[Int]]("tier6_min", O.Default(None))
    /** Database column tier6_max SqlType(INT), Default(None) */
    val tier6Max: Rep[Option[Int]] = column[Option[Int]]("tier6_max", O.Default(None))
    /** Database column tier7_min SqlType(INT), Default(None) */
    val tier7Min: Rep[Option[Int]] = column[Option[Int]]("tier7_min", O.Default(None))
    /** Database column tier7_max SqlType(INT), Default(None) */
    val tier7Max: Rep[Option[Int]] = column[Option[Int]]("tier7_max", O.Default(None))
    /** Database column tier8_min SqlType(INT), Default(None) */
    val tier8Min: Rep[Option[Int]] = column[Option[Int]]("tier8_min", O.Default(None))
    /** Database column tier8_max SqlType(INT), Default(None) */
    val tier8Max: Rep[Option[Int]] = column[Option[Int]]("tier8_max", O.Default(None))
    /** Database column tier9_min SqlType(INT), Default(None) */
    val tier9Min: Rep[Option[Int]] = column[Option[Int]]("tier9_min", O.Default(None))
    /** Database column tier9_max SqlType(INT), Default(None) */
    val tier9Max: Rep[Option[Int]] = column[Option[Int]]("tier9_max", O.Default(None))
    /** Database column exam_id SqlType(INT) */
    val examId: Rep[Int] = column[Int]("exam_id")
    /** Database column subject_id SqlType(INT) */
    val subjectId: Rep[Int] = column[Int]("subject_id")
    /** Database column exam_record_type_id SqlType(INT) */
    val examRecordTypeId: Rep[Int] = column[Int]("exam_record_type_id")

    /** Foreign key referencing Exam (database name Exam_criteria_ibfk_1) */
    lazy val examFk = foreignKey("Exam_criteria_ibfk_1", examId, Exam)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing ExamRecordType (database name Exam_criteria_ibfk_2) */
    lazy val examRecordTypeFk = foreignKey("Exam_criteria_ibfk_2", examRecordTypeId, ExamRecordType)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Subjects (database name Exam_criteria_ibfk_3) */
    lazy val subjectsFk = foreignKey("Exam_criteria_ibfk_3", subjectId, Subjects)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table ExamCriteria */
  lazy val ExamCriteria = new TableQuery(tag => new ExamCriteria(tag))

  /** Entity class storing rows of table ExamRecords
   *  @param studentId Database column student_id SqlType(INT)
   *  @param score Database column score SqlType(INT), Default(None)
   *  @param subjectId Database column subject_id SqlType(INT)
   *  @param examId Database column exam_id SqlType(INT), Default(None)
   *  @param recordTypeId Database column record_type_id SqlType(INT), Default(None) */
  case class ExamRecordsRow(studentId: Int, score: Option[Int] = None, subjectId: Int, examId: Option[Int] = None, recordTypeId: Option[Int] = None)
  /** GetResult implicit for fetching ExamRecordsRow objects using plain SQL queries */
  implicit def GetResultExamRecordsRow(implicit e0: GR[Int], e1: GR[Option[Int]]): GR[ExamRecordsRow] = GR{
    prs => import prs._
    ExamRecordsRow.tupled((<<[Int], <<?[Int], <<[Int], <<?[Int], <<?[Int]))
  }
  /** Table description of table Exam_records. Objects of this class serve as prototypes for rows in queries. */
  class ExamRecords(_tableTag: Tag) extends Table[ExamRecordsRow](_tableTag, "Exam_records") {
    def * = (studentId, score, subjectId, examId, recordTypeId) <> (ExamRecordsRow.tupled, ExamRecordsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(studentId), score, Rep.Some(subjectId), examId, recordTypeId).shaped.<>({r=>import r._; _1.map(_=> ExamRecordsRow.tupled((_1.get, _2, _3.get, _4, _5)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column student_id SqlType(INT) */
    val studentId: Rep[Int] = column[Int]("student_id")
    /** Database column score SqlType(INT), Default(None) */
    val score: Rep[Option[Int]] = column[Option[Int]]("score", O.Default(None))
    /** Database column subject_id SqlType(INT) */
    val subjectId: Rep[Int] = column[Int]("subject_id")
    /** Database column exam_id SqlType(INT), Default(None) */
    val examId: Rep[Option[Int]] = column[Option[Int]]("exam_id", O.Default(None))
    /** Database column record_type_id SqlType(INT), Default(None) */
    val recordTypeId: Rep[Option[Int]] = column[Option[Int]]("record_type_id", O.Default(None))

    /** Foreign key referencing Students (database name Exam_records_ibfk_3) */
    lazy val studentsFk = foreignKey("Exam_records_ibfk_3", studentId, Students)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table ExamRecords */
  lazy val ExamRecords = new TableQuery(tag => new ExamRecords(tag))

  /** Entity class storing rows of table ExamRecordType
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param `type` Database column type SqlType(VARCHAR), Length(255,true), Default(None) */
  case class ExamRecordTypeRow(id: Int, `type`: Option[String] = None)
  /** GetResult implicit for fetching ExamRecordTypeRow objects using plain SQL queries */
  implicit def GetResultExamRecordTypeRow(implicit e0: GR[Int], e1: GR[Option[String]]): GR[ExamRecordTypeRow] = GR{
    prs => import prs._
    ExamRecordTypeRow.tupled((<<[Int], <<?[String]))
  }
  /** Table description of table Exam_record_type. Objects of this class serve as prototypes for rows in queries.
   *  NOTE: The following names collided with Scala keywords and were escaped: type */
  class ExamRecordType(_tableTag: Tag) extends Table[ExamRecordTypeRow](_tableTag, "Exam_record_type") {
    def * = (id, `type`) <> (ExamRecordTypeRow.tupled, ExamRecordTypeRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), `type`).shaped.<>({r=>import r._; _1.map(_=> ExamRecordTypeRow.tupled((_1.get, _2)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column type SqlType(VARCHAR), Length(255,true), Default(None)
     *  NOTE: The name was escaped because it collided with a Scala keyword. */
    val `type`: Rep[Option[String]] = column[Option[String]]("type", O.Length(255,varying=true), O.Default(None))
  }
  /** Collection-like TableQuery object for table ExamRecordType */
  lazy val ExamRecordType = new TableQuery(tag => new ExamRecordType(tag))

  /** Entity class storing rows of table LectureList
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param title Database column title SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param contents Database column contents SqlType(BLOB), Default(None)
   *  @param classTime Database column class_time SqlType(DATE), Default(None)
   *  @param teacherId Database column teacher_id SqlType(INT)
   *  @param textbookId Database column textbook_id SqlType(INT) */
  case class LectureListRow(id: Int, title: Option[String] = None, contents: Option[java.sql.Blob] = None, classTime: Option[java.sql.Date] = None, teacherId: Int, textbookId: Int)
  /** GetResult implicit for fetching LectureListRow objects using plain SQL queries */
  implicit def GetResultLectureListRow(implicit e0: GR[Int], e1: GR[Option[String]], e2: GR[Option[java.sql.Blob]], e3: GR[Option[java.sql.Date]]): GR[LectureListRow] = GR{
    prs => import prs._
    LectureListRow.tupled((<<[Int], <<?[String], <<?[java.sql.Blob], <<?[java.sql.Date], <<[Int], <<[Int]))
  }
  /** Table description of table Lecture_list. Objects of this class serve as prototypes for rows in queries. */
  class LectureList(_tableTag: Tag) extends Table[LectureListRow](_tableTag, "Lecture_list") {
    def * = (id, title, contents, classTime, teacherId, textbookId) <> (LectureListRow.tupled, LectureListRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), title, contents, classTime, Rep.Some(teacherId), Rep.Some(textbookId)).shaped.<>({r=>import r._; _1.map(_=> LectureListRow.tupled((_1.get, _2, _3, _4, _5.get, _6.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column title SqlType(VARCHAR), Length(255,true), Default(None) */
    val title: Rep[Option[String]] = column[Option[String]]("title", O.Length(255,varying=true), O.Default(None))
    /** Database column contents SqlType(BLOB), Default(None) */
    val contents: Rep[Option[java.sql.Blob]] = column[Option[java.sql.Blob]]("contents", O.Default(None))
    /** Database column class_time SqlType(DATE), Default(None) */
    val classTime: Rep[Option[java.sql.Date]] = column[Option[java.sql.Date]]("class_time", O.Default(None))
    /** Database column teacher_id SqlType(INT) */
    val teacherId: Rep[Int] = column[Int]("teacher_id")
    /** Database column textbook_id SqlType(INT) */
    val textbookId: Rep[Int] = column[Int]("textbook_id")

    /** Foreign key referencing Teachers (database name Lecture_list_ibfk_1) */
    lazy val teachersFk = foreignKey("Lecture_list_ibfk_1", teacherId, Teachers)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Textbooks (database name Lecture_list_ibfk_2) */
    lazy val textbooksFk = foreignKey("Lecture_list_ibfk_2", textbookId, Textbooks)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table LectureList */
  lazy val LectureList = new TableQuery(tag => new LectureList(tag))

  /** Entity class storing rows of table LectureRecords
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param createdAt Database column created_at SqlType(DATE), Default(None)
   *  @param startTime Database column start_time SqlType(TIME), Default(None)
   *  @param endTime Database column end_time SqlType(TIME), Default(None)
   *  @param duration Database column duration SqlType(TIME), Default(None)
   *  @param lectureId Database column lecture_id SqlType(INT) */
  case class LectureRecordsRow(id: Int, createdAt: Option[java.sql.Date] = None, startTime: Option[java.sql.Time] = None, endTime: Option[java.sql.Time] = None, duration: Option[java.sql.Time] = None, lectureId: Int)
  /** GetResult implicit for fetching LectureRecordsRow objects using plain SQL queries */
  implicit def GetResultLectureRecordsRow(implicit e0: GR[Int], e1: GR[Option[java.sql.Date]], e2: GR[Option[java.sql.Time]]): GR[LectureRecordsRow] = GR{
    prs => import prs._
    LectureRecordsRow.tupled((<<[Int], <<?[java.sql.Date], <<?[java.sql.Time], <<?[java.sql.Time], <<?[java.sql.Time], <<[Int]))
  }
  /** Table description of table Lecture_records. Objects of this class serve as prototypes for rows in queries. */
  class LectureRecords(_tableTag: Tag) extends Table[LectureRecordsRow](_tableTag, "Lecture_records") {
    def * = (id, createdAt, startTime, endTime, duration, lectureId) <> (LectureRecordsRow.tupled, LectureRecordsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), createdAt, startTime, endTime, duration, Rep.Some(lectureId)).shaped.<>({r=>import r._; _1.map(_=> LectureRecordsRow.tupled((_1.get, _2, _3, _4, _5, _6.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column created_at SqlType(DATE), Default(None) */
    val createdAt: Rep[Option[java.sql.Date]] = column[Option[java.sql.Date]]("created_at", O.Default(None))
    /** Database column start_time SqlType(TIME), Default(None) */
    val startTime: Rep[Option[java.sql.Time]] = column[Option[java.sql.Time]]("start_time", O.Default(None))
    /** Database column end_time SqlType(TIME), Default(None) */
    val endTime: Rep[Option[java.sql.Time]] = column[Option[java.sql.Time]]("end_time", O.Default(None))
    /** Database column duration SqlType(TIME), Default(None) */
    val duration: Rep[Option[java.sql.Time]] = column[Option[java.sql.Time]]("duration", O.Default(None))
    /** Database column lecture_id SqlType(INT) */
    val lectureId: Rep[Int] = column[Int]("lecture_id")

    /** Foreign key referencing Lectures (database name Lecture_records_ibfk_1) */
    lazy val lecturesFk = foreignKey("Lecture_records_ibfk_1", lectureId, Lectures)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)

    /** Uniqueness Index over (lectureId) (database name lecture_id) */
    val index1 = index("lecture_id", lectureId, unique=true)
  }
  /** Collection-like TableQuery object for table LectureRecords */
  lazy val LectureRecords = new TableQuery(tag => new LectureRecords(tag))

  /** Entity class storing rows of table Lectures
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param title Database column title SqlType(TEXT), Default(None)
   *  @param instructor Database column instructor SqlType(VARCHAR), Length(255,true), Default(None) */
  case class LecturesRow(id: Int, title: Option[String] = None, instructor: Option[String] = None)
  /** GetResult implicit for fetching LecturesRow objects using plain SQL queries */
  implicit def GetResultLecturesRow(implicit e0: GR[Int], e1: GR[Option[String]]): GR[LecturesRow] = GR{
    prs => import prs._
    LecturesRow.tupled((<<[Int], <<?[String], <<?[String]))
  }
  /** Table description of table Lectures. Objects of this class serve as prototypes for rows in queries. */
  class Lectures(_tableTag: Tag) extends Table[LecturesRow](_tableTag, "Lectures") {
    def * = (id, title, instructor) <> (LecturesRow.tupled, LecturesRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), title, instructor).shaped.<>({r=>import r._; _1.map(_=> LecturesRow.tupled((_1.get, _2, _3)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column title SqlType(TEXT), Default(None) */
    val title: Rep[Option[String]] = column[Option[String]]("title", O.Default(None))
    /** Database column instructor SqlType(VARCHAR), Length(255,true), Default(None) */
    val instructor: Rep[Option[String]] = column[Option[String]]("instructor", O.Length(255,varying=true), O.Default(None))
  }
  /** Collection-like TableQuery object for table Lectures */
  lazy val Lectures = new TableQuery(tag => new Lectures(tag))

  /** Entity class storing rows of table Mentors
   *  @param id Database column id SqlType(INT), PrimaryKey
   *  @param major Database column major SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param highSchool Database column high_school SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param university Database column university SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param goal Database column goal SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param introduce Database column introduce SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param studyMethod Database column study_method SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param guidancePlan Database column guidance_plan SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param numOfAnswer Database column num_of_answer SqlType(INT), Default(None)
   *  @param point Database column point SqlType(INT), Default(None)
   *  @param grade Database column grade SqlType(FLOAT), Default(None)
   *  @param isAuthenticated Database column is_authenticated SqlType(BIT), Default(None)
   *  @param nickname Database column nickname SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param year Database column year SqlType(INT), Default(None)
   *  @param cardImage Database column card_image SqlType(VARCHAR), Length(255,true), Default(None) */
  case class MentorsRow(id: Int, major: Option[String] = None, highSchool: Option[String] = None, university: Option[String] = None, goal: Option[String] = None, introduce: Option[String] = None, studyMethod: Option[String] = None, guidancePlan: Option[String] = None, numOfAnswer: Option[Int] = None, point: Option[Int] = None, grade: Option[Float] = None, isAuthenticated: Option[Boolean] = None, nickname: Option[String] = None, year: Option[Int] = None, cardImage: Option[String] = None)
  /** GetResult implicit for fetching MentorsRow objects using plain SQL queries */
  implicit def GetResultMentorsRow(implicit e0: GR[Int], e1: GR[Option[String]], e2: GR[Option[Int]], e3: GR[Option[Float]], e4: GR[Option[Boolean]]): GR[MentorsRow] = GR{
    prs => import prs._
    MentorsRow.tupled((<<[Int], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[Int], <<?[Int], <<?[Float], <<?[Boolean], <<?[String], <<?[Int], <<?[String]))
  }
  /** Table description of table Mentors. Objects of this class serve as prototypes for rows in queries. */
  class Mentors(_tableTag: Tag) extends Table[MentorsRow](_tableTag, "Mentors") {
    def * = (id, major, highSchool, university, goal, introduce, studyMethod, guidancePlan, numOfAnswer, point, grade, isAuthenticated, nickname, year, cardImage) <> (MentorsRow.tupled, MentorsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), major, highSchool, university, goal, introduce, studyMethod, guidancePlan, numOfAnswer, point, grade, isAuthenticated, nickname, year, cardImage).shaped.<>({r=>import r._; _1.map(_=> MentorsRow.tupled((_1.get, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.PrimaryKey)
    /** Database column major SqlType(VARCHAR), Length(255,true), Default(None) */
    val major: Rep[Option[String]] = column[Option[String]]("major", O.Length(255,varying=true), O.Default(None))
    /** Database column high_school SqlType(VARCHAR), Length(255,true), Default(None) */
    val highSchool: Rep[Option[String]] = column[Option[String]]("high_school", O.Length(255,varying=true), O.Default(None))
    /** Database column university SqlType(VARCHAR), Length(255,true), Default(None) */
    val university: Rep[Option[String]] = column[Option[String]]("university", O.Length(255,varying=true), O.Default(None))
    /** Database column goal SqlType(VARCHAR), Length(255,true), Default(None) */
    val goal: Rep[Option[String]] = column[Option[String]]("goal", O.Length(255,varying=true), O.Default(None))
    /** Database column introduce SqlType(VARCHAR), Length(255,true), Default(None) */
    val introduce: Rep[Option[String]] = column[Option[String]]("introduce", O.Length(255,varying=true), O.Default(None))
    /** Database column study_method SqlType(VARCHAR), Length(255,true), Default(None) */
    val studyMethod: Rep[Option[String]] = column[Option[String]]("study_method", O.Length(255,varying=true), O.Default(None))
    /** Database column guidance_plan SqlType(VARCHAR), Length(255,true), Default(None) */
    val guidancePlan: Rep[Option[String]] = column[Option[String]]("guidance_plan", O.Length(255,varying=true), O.Default(None))
    /** Database column num_of_answer SqlType(INT), Default(None) */
    val numOfAnswer: Rep[Option[Int]] = column[Option[Int]]("num_of_answer", O.Default(None))
    /** Database column point SqlType(INT), Default(None) */
    val point: Rep[Option[Int]] = column[Option[Int]]("point", O.Default(None))
    /** Database column grade SqlType(FLOAT), Default(None) */
    val grade: Rep[Option[Float]] = column[Option[Float]]("grade", O.Default(None))
    /** Database column is_authenticated SqlType(BIT), Default(None) */
    val isAuthenticated: Rep[Option[Boolean]] = column[Option[Boolean]]("is_authenticated", O.Default(None))
    /** Database column nickname SqlType(VARCHAR), Length(255,true), Default(None) */
    val nickname: Rep[Option[String]] = column[Option[String]]("nickname", O.Length(255,varying=true), O.Default(None))
    /** Database column year SqlType(INT), Default(None) */
    val year: Rep[Option[Int]] = column[Option[Int]]("year", O.Default(None))
    /** Database column card_image SqlType(VARCHAR), Length(255,true), Default(None) */
    val cardImage: Rep[Option[String]] = column[Option[String]]("card_image", O.Length(255,varying=true), O.Default(None))

    /** Foreign key referencing Users (database name Mentors_ibfk_1) */
    lazy val usersFk = foreignKey("Mentors_ibfk_1", id, Users)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table Mentors */
  lazy val Mentors = new TableQuery(tag => new Mentors(tag))

  /** Entity class storing rows of table MentorSubjects
   *  @param mentorId Database column mentor_id SqlType(INT)
   *  @param subjectId Database column subject_id SqlType(INT) */
  case class MentorSubjectsRow(mentorId: Int, subjectId: Int)
  /** GetResult implicit for fetching MentorSubjectsRow objects using plain SQL queries */
  implicit def GetResultMentorSubjectsRow(implicit e0: GR[Int]): GR[MentorSubjectsRow] = GR{
    prs => import prs._
    MentorSubjectsRow.tupled((<<[Int], <<[Int]))
  }
  /** Table description of table Mentor_subjects. Objects of this class serve as prototypes for rows in queries. */
  class MentorSubjects(_tableTag: Tag) extends Table[MentorSubjectsRow](_tableTag, "Mentor_subjects") {
    def * = (mentorId, subjectId) <> (MentorSubjectsRow.tupled, MentorSubjectsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(mentorId), Rep.Some(subjectId)).shaped.<>({r=>import r._; _1.map(_=> MentorSubjectsRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column mentor_id SqlType(INT) */
    val mentorId: Rep[Int] = column[Int]("mentor_id")
    /** Database column subject_id SqlType(INT) */
    val subjectId: Rep[Int] = column[Int]("subject_id")

    /** Foreign key referencing Mentors (database name Mentor_subjects_ibfk_1) */
    lazy val mentorsFk = foreignKey("Mentor_subjects_ibfk_1", mentorId, Mentors)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Subjects (database name Mentor_subjects_ibfk_2) */
    lazy val subjectsFk = foreignKey("Mentor_subjects_ibfk_2", subjectId, Subjects)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)

    /** Uniqueness Index over (mentorId,subjectId) (database name mentor_id) */
    val index1 = index("mentor_id", (mentorId, subjectId), unique=true)
  }
  /** Collection-like TableQuery object for table MentorSubjects */
  lazy val MentorSubjects = new TableQuery(tag => new MentorSubjects(tag))

  /** Entity class storing rows of table Messages
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param message Database column message SqlType(TEXT), Default(None)
   *  @param createdAt Database column created_at SqlType(DATETIME), Default(None)
   *  @param senderId Database column sender_id SqlType(INT)
   *  @param receiverId Database column receiver_id SqlType(INT)
   *  @param isNew Database column is_new SqlType(BIT), Default(None)
   *  @param title Database column title SqlType(VARCHAR), Length(255,true), Default(None) */
  case class MessagesRow(id: Int, message: Option[String] = None, createdAt: Option[java.sql.Timestamp] = None, senderId: Int, receiverId: Int, isNew: Option[Boolean] = None, title: Option[String] = None)
  /** GetResult implicit for fetching MessagesRow objects using plain SQL queries */
  implicit def GetResultMessagesRow(implicit e0: GR[Int], e1: GR[Option[String]], e2: GR[Option[java.sql.Timestamp]], e3: GR[Option[Boolean]]): GR[MessagesRow] = GR{
    prs => import prs._
    MessagesRow.tupled((<<[Int], <<?[String], <<?[java.sql.Timestamp], <<[Int], <<[Int], <<?[Boolean], <<?[String]))
  }
  /** Table description of table Messages. Objects of this class serve as prototypes for rows in queries. */
  class Messages(_tableTag: Tag) extends Table[MessagesRow](_tableTag, "Messages") {
    def * = (id, message, createdAt, senderId, receiverId, isNew, title) <> (MessagesRow.tupled, MessagesRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), message, createdAt, Rep.Some(senderId), Rep.Some(receiverId), isNew, title).shaped.<>({r=>import r._; _1.map(_=> MessagesRow.tupled((_1.get, _2, _3, _4.get, _5.get, _6, _7)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column message SqlType(TEXT), Default(None) */
    val message: Rep[Option[String]] = column[Option[String]]("message", O.Default(None))
    /** Database column created_at SqlType(DATETIME), Default(None) */
    val createdAt: Rep[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("created_at", O.Default(None))
    /** Database column sender_id SqlType(INT) */
    val senderId: Rep[Int] = column[Int]("sender_id")
    /** Database column receiver_id SqlType(INT) */
    val receiverId: Rep[Int] = column[Int]("receiver_id")
    /** Database column is_new SqlType(BIT), Default(None) */
    val isNew: Rep[Option[Boolean]] = column[Option[Boolean]]("is_new", O.Default(None))
    /** Database column title SqlType(VARCHAR), Length(255,true), Default(None) */
    val title: Rep[Option[String]] = column[Option[String]]("title", O.Length(255,varying=true), O.Default(None))

    /** Foreign key referencing Users (database name Messages_ibfk_1) */
    lazy val usersFk1 = foreignKey("Messages_ibfk_1", senderId, Users)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Users (database name Messages_ibfk_2) */
    lazy val usersFk2 = foreignKey("Messages_ibfk_2", receiverId, Users)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table Messages */
  lazy val Messages = new TableQuery(tag => new Messages(tag))

  /** Entity class storing rows of table MSMatching
   *  @param studentId Database column student_id SqlType(INT)
   *  @param mentorId Database column mentor_id SqlType(INT) */
  case class MSMatchingRow(studentId: Int, mentorId: Int)
  /** GetResult implicit for fetching MSMatchingRow objects using plain SQL queries */
  implicit def GetResultMSMatchingRow(implicit e0: GR[Int]): GR[MSMatchingRow] = GR{
    prs => import prs._
    MSMatchingRow.tupled((<<[Int], <<[Int]))
  }
  /** Table description of table M_S_Matching. Objects of this class serve as prototypes for rows in queries. */
  class MSMatching(_tableTag: Tag) extends Table[MSMatchingRow](_tableTag, "M_S_Matching") {
    def * = (studentId, mentorId) <> (MSMatchingRow.tupled, MSMatchingRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(studentId), Rep.Some(mentorId)).shaped.<>({r=>import r._; _1.map(_=> MSMatchingRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column student_id SqlType(INT) */
    val studentId: Rep[Int] = column[Int]("student_id")
    /** Database column mentor_id SqlType(INT) */
    val mentorId: Rep[Int] = column[Int]("mentor_id")

    /** Foreign key referencing Mentors (database name M_S_Matching_ibfk_1) */
    lazy val mentorsFk = foreignKey("M_S_Matching_ibfk_1", mentorId, Mentors)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Students (database name M_S_Matching_ibfk_2) */
    lazy val studentsFk = foreignKey("M_S_Matching_ibfk_2", studentId, Students)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)

    /** Uniqueness Index over (studentId,mentorId) (database name student_id) */
    val index1 = index("student_id", (studentId, mentorId), unique=true)
  }
  /** Collection-like TableQuery object for table MSMatching */
  lazy val MSMatching = new TableQuery(tag => new MSMatching(tag))

  /** Entity class storing rows of table Notices
    *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
    *  @param title Database column title SqlType(VARCHAR), Length(255,true)
    *  @param article Database column article SqlType(TEXT)
    *  @param createdAt Database column created_at SqlType(TIMESTAMP) */
  case class NoticesRow(id: Int, title: String, article: String, createdAt: java.sql.Timestamp)
  /** GetResult implicit for fetching NoticesRow objects using plain SQL queries */
  implicit def GetResultNoticesRow(implicit e0: GR[Int], e1: GR[String], e2: GR[java.sql.Timestamp]): GR[NoticesRow] = GR{
    prs => import prs._
      NoticesRow.tupled((<<[Int], <<[String], <<[String], <<[java.sql.Timestamp]))
  }
  /** Table description of table Notices. Objects of this class serve as prototypes for rows in queries. */
  class Notices(_tableTag: Tag) extends Table[NoticesRow](_tableTag, "Notices") {
    def * = (id, title, article, createdAt) <> (NoticesRow.tupled, NoticesRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(title), Rep.Some(article), Rep.Some(createdAt)).shaped.<>({r=>import r._; _1.map(_=> NoticesRow.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column title SqlType(VARCHAR), Length(255,true) */
    val title: Rep[String] = column[String]("title", O.Length(255,varying=true))
    /** Database column article SqlType(TEXT) */
    val article: Rep[String] = column[String]("article")
    /** Database column created_at SqlType(TIMESTAMP) */
    val createdAt: Rep[java.sql.Timestamp] = column[java.sql.Timestamp]("created_at")
  }
  /** Collection-like TableQuery object for table Notices */
  lazy val Notices = new TableQuery(tag => new Notices(tag))

  /** Entity class storing rows of table OptionalSubjects
   *  @param studentId Database column student_id SqlType(INT)
   *  @param subjectId Database column subject_id SqlType(INT) */
  case class OptionalSubjectsRow(studentId: Int, subjectId: Int)
  /** GetResult implicit for fetching OptionalSubjectsRow objects using plain SQL queries */
  implicit def GetResultOptionalSubjectsRow(implicit e0: GR[Int]): GR[OptionalSubjectsRow] = GR{
    prs => import prs._
    OptionalSubjectsRow.tupled((<<[Int], <<[Int]))
  }
  /** Table description of table Optional_subjects. Objects of this class serve as prototypes for rows in queries. */
  class OptionalSubjects(_tableTag: Tag) extends Table[OptionalSubjectsRow](_tableTag, "Optional_subjects") {
    def * = (studentId, subjectId) <> (OptionalSubjectsRow.tupled, OptionalSubjectsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(studentId), Rep.Some(subjectId)).shaped.<>({r=>import r._; _1.map(_=> OptionalSubjectsRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column student_id SqlType(INT) */
    val studentId: Rep[Int] = column[Int]("student_id")
    /** Database column subject_id SqlType(INT) */
    val subjectId: Rep[Int] = column[Int]("subject_id")

    /** Foreign key referencing Students (database name Optional_subjects_ibfk_1) */
    lazy val studentsFk = foreignKey("Optional_subjects_ibfk_1", studentId, Students)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Subjects (database name Optional_subjects_ibfk_2) */
    lazy val subjectsFk = foreignKey("Optional_subjects_ibfk_2", subjectId, Subjects)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)

    /** Uniqueness Index over (studentId,subjectId) (database name student_id) */
    val index1 = index("student_id", (studentId, subjectId), unique=true)
  }
  /** Collection-like TableQuery object for table OptionalSubjects */
  lazy val OptionalSubjects = new TableQuery(tag => new OptionalSubjects(tag))

  /** Entity class storing rows of table PostingImages
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param imageUrl Database column image_url SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param postingId Database column posting_id SqlType(INT) */
  case class PostingImagesRow(id: Int, imageUrl: Option[String] = None, postingId: Int)
  /** GetResult implicit for fetching PostingImagesRow objects using plain SQL queries */
  implicit def GetResultPostingImagesRow(implicit e0: GR[Int], e1: GR[Option[String]]): GR[PostingImagesRow] = GR{
    prs => import prs._
    PostingImagesRow.tupled((<<[Int], <<?[String], <<[Int]))
  }
  /** Table description of table Posting_images. Objects of this class serve as prototypes for rows in queries. */
  class PostingImages(_tableTag: Tag) extends Table[PostingImagesRow](_tableTag, "Posting_images") {
    def * = (id, imageUrl, postingId) <> (PostingImagesRow.tupled, PostingImagesRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), imageUrl, Rep.Some(postingId)).shaped.<>({r=>import r._; _1.map(_=> PostingImagesRow.tupled((_1.get, _2, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column image_url SqlType(VARCHAR), Length(255,true), Default(None) */
    val imageUrl: Rep[Option[String]] = column[Option[String]]("image_url", O.Length(255,varying=true), O.Default(None))
    /** Database column posting_id SqlType(INT) */
    val postingId: Rep[Int] = column[Int]("posting_id")

    /** Foreign key referencing Postings (database name Posting_images_ibfk_1) */
    lazy val postingsFk = foreignKey("Posting_images_ibfk_1", postingId, Postings)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table PostingImages */
  lazy val PostingImages = new TableQuery(tag => new PostingImages(tag))

  /** Entity class storing rows of table PostingReport
   *  @param postingId Database column posting_id SqlType(INT)
   *  @param userId Database column user_id SqlType(INT) */
  case class PostingReportRow(postingId: Int, userId: Int)
  /** GetResult implicit for fetching PostingReportRow objects using plain SQL queries */
  implicit def GetResultPostingReportRow(implicit e0: GR[Int]): GR[PostingReportRow] = GR{
    prs => import prs._
    PostingReportRow.tupled((<<[Int], <<[Int]))
  }
  /** Table description of table Posting_report. Objects of this class serve as prototypes for rows in queries. */
  class PostingReport(_tableTag: Tag) extends Table[PostingReportRow](_tableTag, "Posting_report") {
    def * = (postingId, userId) <> (PostingReportRow.tupled, PostingReportRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(postingId), Rep.Some(userId)).shaped.<>({r=>import r._; _1.map(_=> PostingReportRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column posting_id SqlType(INT) */
    val postingId: Rep[Int] = column[Int]("posting_id")
    /** Database column user_id SqlType(INT) */
    val userId: Rep[Int] = column[Int]("user_id")

    /** Foreign key referencing Postings (database name Posting_report_ibfk_1) */
    lazy val postingsFk = foreignKey("Posting_report_ibfk_1", postingId, Postings)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Users (database name Posting_report_ibfk_2) */
    lazy val usersFk = foreignKey("Posting_report_ibfk_2", userId, Users)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table PostingReport */
  lazy val PostingReport = new TableQuery(tag => new PostingReport(tag))

  /** Entity class storing rows of table Postings
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param title Database column title SqlType(TEXT), Default(None)
   *  @param article Database column article SqlType(TEXT), Default(None)
   *  @param createdAt Database column created_at SqlType(DATETIME), Default(None)
   *  @param isAnswered Database column is_answered SqlType(BIT), Default(None)
   *  @param parentId Database column parent_id SqlType(INT), Default(None)
   *  @param numOfReply Database column num_of_reply SqlType(INT), Default(None)
   *  @param userId Database column user_id SqlType(INT)
   *  @param typeId Database column type_id SqlType(INT)
   *  @param textbookId Database column textbook_id SqlType(INT), Default(None)
   *  @param textbookPage Database column textbook_page SqlType(INT), Default(None)
   *  @param questionNumber Database column question_number SqlType(INT), Default(None)
   *  @param isChosen Database column is_chosen SqlType(BIT), Default(None)
   *  @param score Database column score SqlType(INT), Default(None)
   *  @param subjectId Database column subject_id SqlType(INT), Default(None)
   *  @param hasNewAnswer Database column has_new_answer SqlType(BIT), Default(None)
   *  @param isDeleted Database column is_deleted SqlType(BIT), Default(None)
   *  @param updatedAt Database column updated_at SqlType(DATETIME), Default(None)
   *  @param subSubject Database column sub_subject SqlType(INT), Default(None)
   *  @param customTextbookId Database column custom_textbook_id SqlType(INT), Default(None) */
  case class PostingsRow(id: Int, title: Option[String] = None, article: Option[String] = None, createdAt: Option[java.sql.Timestamp] = None, isAnswered: Option[Boolean] = None, parentId: Option[Int] = None, numOfReply: Option[Int] = None, userId: Int, typeId: Int, textbookId: Option[Int] = None, textbookPage: Option[Int] = None, questionNumber: Option[Int] = None, isChosen: Option[Boolean] = None, score: Option[Int] = None, subjectId: Option[Int] = None, hasNewAnswer: Option[Boolean] = None, isDeleted: Option[Boolean] = None, updatedAt: Option[java.sql.Timestamp] = None, subSubject: Option[Int] = None, customTextbookId: Option[Int] = None)
  /** GetResult implicit for fetching PostingsRow objects using plain SQL queries */
  implicit def GetResultPostingsRow(implicit e0: GR[Int], e1: GR[Option[String]], e2: GR[Option[java.sql.Timestamp]], e3: GR[Option[Boolean]], e4: GR[Option[Int]]): GR[PostingsRow] = GR{
    prs => import prs._
    PostingsRow.tupled((<<[Int], <<?[String], <<?[String], <<?[java.sql.Timestamp], <<?[Boolean], <<?[Int], <<?[Int], <<[Int], <<[Int], <<?[Int], <<?[Int], <<?[Int], <<?[Boolean], <<?[Int], <<?[Int], <<?[Boolean], <<?[Boolean], <<?[java.sql.Timestamp], <<?[Int], <<?[Int]))
  }
  /** Table description of table Postings. Objects of this class serve as prototypes for rows in queries. */
  class Postings(_tableTag: Tag) extends Table[PostingsRow](_tableTag, "Postings") {
    def * = (id, title, article, createdAt, isAnswered, parentId, numOfReply, userId, typeId, textbookId, textbookPage, questionNumber, isChosen, score, subjectId, hasNewAnswer, isDeleted, updatedAt, subSubject, customTextbookId) <> (PostingsRow.tupled, PostingsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), title, article, createdAt, isAnswered, parentId, numOfReply, Rep.Some(userId), Rep.Some(typeId), textbookId, textbookPage, questionNumber, isChosen, score, subjectId, hasNewAnswer, isDeleted, updatedAt, subSubject, customTextbookId).shaped.<>({r=>import r._; _1.map(_=> PostingsRow.tupled((_1.get, _2, _3, _4, _5, _6, _7, _8.get, _9.get, _10, _11, _12, _13, _14, _15, _16, _17, _18, _19, _20)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column title SqlType(TEXT), Default(None) */
    val title: Rep[Option[String]] = column[Option[String]]("title", O.Default(None))
    /** Database column article SqlType(TEXT), Default(None) */
    val article: Rep[Option[String]] = column[Option[String]]("article", O.Default(None))
    /** Database column created_at SqlType(DATETIME), Default(None) */
    val createdAt: Rep[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("created_at", O.Default(None))
    /** Database column is_answered SqlType(BIT), Default(None) */
    val isAnswered: Rep[Option[Boolean]] = column[Option[Boolean]]("is_answered", O.Default(None))
    /** Database column parent_id SqlType(INT), Default(None) */
    val parentId: Rep[Option[Int]] = column[Option[Int]]("parent_id", O.Default(None))
    /** Database column num_of_reply SqlType(INT), Default(None) */
    val numOfReply: Rep[Option[Int]] = column[Option[Int]]("num_of_reply", O.Default(None))
    /** Database column user_id SqlType(INT) */
    val userId: Rep[Int] = column[Int]("user_id")
    /** Database column type_id SqlType(INT) */
    val typeId: Rep[Int] = column[Int]("type_id")
    /** Database column textbook_id SqlType(INT), Default(None) */
    val textbookId: Rep[Option[Int]] = column[Option[Int]]("textbook_id", O.Default(None))
    /** Database column textbook_page SqlType(INT), Default(None) */
    val textbookPage: Rep[Option[Int]] = column[Option[Int]]("textbook_page", O.Default(None))
    /** Database column question_number SqlType(INT), Default(None) */
    val questionNumber: Rep[Option[Int]] = column[Option[Int]]("question_number", O.Default(None))
    /** Database column is_chosen SqlType(BIT), Default(None) */
    val isChosen: Rep[Option[Boolean]] = column[Option[Boolean]]("is_chosen", O.Default(None))
    /** Database column score SqlType(INT), Default(None) */
    val score: Rep[Option[Int]] = column[Option[Int]]("score", O.Default(None))
    /** Database column subject_id SqlType(INT), Default(None) */
    val subjectId: Rep[Option[Int]] = column[Option[Int]]("subject_id", O.Default(None))
    /** Database column has_new_answer SqlType(BIT), Default(None) */
    val hasNewAnswer: Rep[Option[Boolean]] = column[Option[Boolean]]("has_new_answer", O.Default(None))
    /** Database column is_deleted SqlType(BIT), Default(None) */
    val isDeleted: Rep[Option[Boolean]] = column[Option[Boolean]]("is_deleted", O.Default(None))
    /** Database column updated_at SqlType(DATETIME), Default(None) */
    val updatedAt: Rep[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("updated_at", O.Default(None))
    /** Database column sub_subject SqlType(INT), Default(None) */
    val subSubject: Rep[Option[Int]] = column[Option[Int]]("sub_subject", O.Default(None))
    /** Database column custom_textbook_id SqlType(INT), Default(None) */
    val customTextbookId: Rep[Option[Int]] = column[Option[Int]]("custom_textbook_id", O.Default(None))

    /** Foreign key referencing PostingType (database name Postings_ibfk_1) */
    lazy val postingTypeFk = foreignKey("Postings_ibfk_1", typeId, PostingType)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Users (database name Postings_ibfk_3) */
    lazy val usersFk = foreignKey("Postings_ibfk_3", userId, Users)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)

    /** Index over (textbookId) (database name textbook_id) */
    val index1 = index("textbook_id", textbookId)
  }
  /** Collection-like TableQuery object for table Postings */
  lazy val Postings = new TableQuery(tag => new Postings(tag))

  /** Entity class storing rows of table PostingType
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param `type` Database column type SqlType(VARCHAR), Length(255,true), Default(None) */
  case class PostingTypeRow(id: Int, `type`: Option[String] = None)
  /** GetResult implicit for fetching PostingTypeRow objects using plain SQL queries */
  implicit def GetResultPostingTypeRow(implicit e0: GR[Int], e1: GR[Option[String]]): GR[PostingTypeRow] = GR{
    prs => import prs._
    PostingTypeRow.tupled((<<[Int], <<?[String]))
  }
  /** Table description of table Posting_type. Objects of this class serve as prototypes for rows in queries.
   *  NOTE: The following names collided with Scala keywords and were escaped: type */
  class PostingType(_tableTag: Tag) extends Table[PostingTypeRow](_tableTag, "Posting_type") {
    def * = (id, `type`) <> (PostingTypeRow.tupled, PostingTypeRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), `type`).shaped.<>({r=>import r._; _1.map(_=> PostingTypeRow.tupled((_1.get, _2)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column type SqlType(VARCHAR), Length(255,true), Default(None)
     *  NOTE: The name was escaped because it collided with a Scala keyword. */
    val `type`: Rep[Option[String]] = column[Option[String]]("type", O.Length(255,varying=true), O.Default(None))
  }
  /** Collection-like TableQuery object for table PostingType */
  lazy val PostingType = new TableQuery(tag => new PostingType(tag))

  /** Entity class storing rows of table PushMessage
    *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
    *  @param message Database column message SqlType(TEXT), Default(None)
    *  @param description Database column description SqlType(VARCHAR), Length(255,true), Default(None)
    *  @param `type` Database column type SqlType(INT), Default(None)
    *  @param day Database column day SqlType(VARCHAR), Length(45,true), Default(None)
    *  @param hour Database column hour SqlType(INT), Default(None)
    *  @param minute Database column minute SqlType(INT), Default(None) */
  case class PushMessageRow(id: Int, message: Option[String] = None, description: Option[String] = None, `type`: Option[Int] = None, day: Option[String] = None, hour: Option[Int] = None, minute: Option[Int] = None)
  /** GetResult implicit for fetching PushMessageRow objects using plain SQL queries */
  implicit def GetResultPushMessageRow(implicit e0: GR[Int], e1: GR[Option[String]], e2: GR[Option[Int]]): GR[PushMessageRow] = GR{
    prs => import prs._
      PushMessageRow.tupled((<<[Int], <<?[String], <<?[String], <<?[Int], <<?[String], <<?[Int], <<?[Int]))
  }
  /** Table description of table Push_message. Objects of this class serve as prototypes for rows in queries.
    *  NOTE: The following names collided with Scala keywords and were escaped: type */
  class PushMessage(_tableTag: Tag) extends Table[PushMessageRow](_tableTag, "Push_message") {
    def * = (id, message, description, `type`, day, hour, minute) <> (PushMessageRow.tupled, PushMessageRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), message, description, `type`, day, hour, minute).shaped.<>({r=>import r._; _1.map(_=> PushMessageRow.tupled((_1.get, _2, _3, _4, _5, _6, _7)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column message SqlType(TEXT), Default(None) */
    val message: Rep[Option[String]] = column[Option[String]]("message", O.Default(None))
    /** Database column description SqlType(VARCHAR), Length(255,true), Default(None) */
    val description: Rep[Option[String]] = column[Option[String]]("description", O.Length(255,varying=true), O.Default(None))
    /** Database column type SqlType(INT), Default(None)
      *  NOTE: The name was escaped because it collided with a Scala keyword. */
    val `type`: Rep[Option[Int]] = column[Option[Int]]("type", O.Default(None))
    /** Database column day SqlType(VARCHAR), Length(45,true), Default(None) */
    val day: Rep[Option[String]] = column[Option[String]]("day", O.Length(45,varying=true), O.Default(None))
    /** Database column hour SqlType(INT), Default(None) */
    val hour: Rep[Option[Int]] = column[Option[Int]]("hour", O.Default(None))
    /** Database column minute SqlType(INT), Default(None) */
    val minute: Rep[Option[Int]] = column[Option[Int]]("minute", O.Default(None))
  }
  /** Collection-like TableQuery object for table PushMessage */
  lazy val PushMessage = new TableQuery(tag => new PushMessage(tag))

  /** Entity class storing rows of table RecommendUniversity
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param university Database column university SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param department Database column department SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param departmentType Database column department_type SqlType(INT), Default(None)
   *  @param score Database column score SqlType(INT), Default(None)
   *  @param numOfOptional Database column num_of_optional SqlType(INT), Default(None) */
  case class RecommendUniversityRow(id: Int, university: Option[String] = None, department: Option[String] = None, departmentType: Option[Int] = None, score: Option[Int] = None, numOfOptional: Option[Int] = None)
  /** GetResult implicit for fetching RecommendUniversityRow objects using plain SQL queries */
  implicit def GetResultRecommendUniversityRow(implicit e0: GR[Int], e1: GR[Option[String]], e2: GR[Option[Int]]): GR[RecommendUniversityRow] = GR{
    prs => import prs._
    RecommendUniversityRow.tupled((<<[Int], <<?[String], <<?[String], <<?[Int], <<?[Int], <<?[Int]))
  }
  /** Table description of table Recommend_university. Objects of this class serve as prototypes for rows in queries. */
  class RecommendUniversity(_tableTag: Tag) extends Table[RecommendUniversityRow](_tableTag, "Recommend_university") {
    def * = (id, university, department, departmentType, score, numOfOptional) <> (RecommendUniversityRow.tupled, RecommendUniversityRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), university, department, departmentType, score, numOfOptional).shaped.<>({r=>import r._; _1.map(_=> RecommendUniversityRow.tupled((_1.get, _2, _3, _4, _5, _6)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column university SqlType(VARCHAR), Length(255,true), Default(None) */
    val university: Rep[Option[String]] = column[Option[String]]("university", O.Length(255,varying=true), O.Default(None))
    /** Database column department SqlType(VARCHAR), Length(255,true), Default(None) */
    val department: Rep[Option[String]] = column[Option[String]]("department", O.Length(255,varying=true), O.Default(None))
    /** Database column department_type SqlType(INT), Default(None) */
    val departmentType: Rep[Option[Int]] = column[Option[Int]]("department_type", O.Default(None))
    /** Database column score SqlType(INT), Default(None) */
    val score: Rep[Option[Int]] = column[Option[Int]]("score", O.Default(None))
    /** Database column num_of_optional SqlType(INT), Default(None) */
    val numOfOptional: Rep[Option[Int]] = column[Option[Int]]("num_of_optional", O.Default(None))
  }
  /** Collection-like TableQuery object for table RecommendUniversity */
  lazy val RecommendUniversity = new TableQuery(tag => new RecommendUniversity(tag))

  /** Entity class storing rows of table RefundHistory
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param mentorId Database column mentor_id SqlType(INT), Default(None)
   *  @param name Database column name SqlType(VARCHAR), Length(45,true), Default(None)
   *  @param account Database column account SqlType(VARCHAR), Length(45,true), Default(None)
   *  @param bank Database column bank SqlType(VARCHAR), Length(45,true), Default(None)
   *  @param amount Database column amount SqlType(INT), Default(None)
   *  @param isApproval Database column is_approval SqlType(BIT), Default(None)
   *  @param createdAt Database column created_at SqlType(DATE), Default(None)
   *  @param updatedAt Database column updated_at SqlType(DATE), Default(None)
   *  @param balance Database column balance SqlType(INT), Default(None) */
  case class RefundHistoryRow(id: Int, mentorId: Option[Int] = None, name: Option[String] = None, account: Option[String] = None, bank: Option[String] = None, amount: Option[Int] = None, isApproval: Option[Boolean] = None, createdAt: Option[java.sql.Date] = None, updatedAt: Option[java.sql.Date] = None, balance: Option[Int] = None)
  /** GetResult implicit for fetching RefundHistoryRow objects using plain SQL queries */
  implicit def GetResultRefundHistoryRow(implicit e0: GR[Int], e1: GR[Option[Int]], e2: GR[Option[String]], e3: GR[Option[Boolean]], e4: GR[Option[java.sql.Date]]): GR[RefundHistoryRow] = GR{
    prs => import prs._
    RefundHistoryRow.tupled((<<[Int], <<?[Int], <<?[String], <<?[String], <<?[String], <<?[Int], <<?[Boolean], <<?[java.sql.Date], <<?[java.sql.Date], <<?[Int]))
  }
  /** Table description of table Refund_history. Objects of this class serve as prototypes for rows in queries. */
  class RefundHistory(_tableTag: Tag) extends Table[RefundHistoryRow](_tableTag, "Refund_history") {
    def * = (id, mentorId, name, account, bank, amount, isApproval, createdAt, updatedAt, balance) <> (RefundHistoryRow.tupled, RefundHistoryRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), mentorId, name, account, bank, amount, isApproval, createdAt, updatedAt, balance).shaped.<>({r=>import r._; _1.map(_=> RefundHistoryRow.tupled((_1.get, _2, _3, _4, _5, _6, _7, _8, _9, _10)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column mentor_id SqlType(INT), Default(None) */
    val mentorId: Rep[Option[Int]] = column[Option[Int]]("mentor_id", O.Default(None))
    /** Database column name SqlType(VARCHAR), Length(45,true), Default(None) */
    val name: Rep[Option[String]] = column[Option[String]]("name", O.Length(45,varying=true), O.Default(None))
    /** Database column account SqlType(VARCHAR), Length(45,true), Default(None) */
    val account: Rep[Option[String]] = column[Option[String]]("account", O.Length(45,varying=true), O.Default(None))
    /** Database column bank SqlType(VARCHAR), Length(45,true), Default(None) */
    val bank: Rep[Option[String]] = column[Option[String]]("bank", O.Length(45,varying=true), O.Default(None))
    /** Database column amount SqlType(INT), Default(None) */
    val amount: Rep[Option[Int]] = column[Option[Int]]("amount", O.Default(None))
    /** Database column is_approval SqlType(BIT), Default(None) */
    val isApproval: Rep[Option[Boolean]] = column[Option[Boolean]]("is_approval", O.Default(None))
    /** Database column created_at SqlType(DATE), Default(None) */
    val createdAt: Rep[Option[java.sql.Date]] = column[Option[java.sql.Date]]("created_at", O.Default(None))
    /** Database column updated_at SqlType(DATE), Default(None) */
    val updatedAt: Rep[Option[java.sql.Date]] = column[Option[java.sql.Date]]("updated_at", O.Default(None))
    /** Database column balance SqlType(INT), Default(None) */
    val balance: Rep[Option[Int]] = column[Option[Int]]("balance", O.Default(None))
  }
  /** Collection-like TableQuery object for table RefundHistory */
  lazy val RefundHistory = new TableQuery(tag => new RefundHistory(tag))

  /** Entity class storing rows of table Replies
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param reply Database column reply SqlType(TEXT), Default(None)
   *  @param createdAt Database column created_at SqlType(DATETIME), Default(None)
   *  @param numOfLike Database column num_of_like SqlType(INT), Default(None)
   *  @param numOfReply Database column num_of_reply SqlType(INT), Default(None)
   *  @param imageUrl Database column image_url SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param parentId Database column parent_id SqlType(INT), Default(None)
   *  @param postingId Database column posting_id SqlType(INT)
   *  @param userId Database column user_id SqlType(INT)
   *  @param isDeleted Database column is_deleted SqlType(BIT), Default(None) */
  case class RepliesRow(id: Int, reply: Option[String] = None, createdAt: Option[java.sql.Timestamp] = None, numOfLike: Option[Int] = None, numOfReply: Option[Int] = None, imageUrl: Option[String] = None, parentId: Option[Int] = None, postingId: Int, userId: Int, isDeleted: Option[Boolean] = None)
  /** GetResult implicit for fetching RepliesRow objects using plain SQL queries */
  implicit def GetResultRepliesRow(implicit e0: GR[Int], e1: GR[Option[String]], e2: GR[Option[java.sql.Timestamp]], e3: GR[Option[Int]], e4: GR[Option[Boolean]]): GR[RepliesRow] = GR{
    prs => import prs._
    RepliesRow.tupled((<<[Int], <<?[String], <<?[java.sql.Timestamp], <<?[Int], <<?[Int], <<?[String], <<?[Int], <<[Int], <<[Int], <<?[Boolean]))
  }
  /** Table description of table Replies. Objects of this class serve as prototypes for rows in queries. */
  class Replies(_tableTag: Tag) extends Table[RepliesRow](_tableTag, "Replies") {
    def * = (id, reply, createdAt, numOfLike, numOfReply, imageUrl, parentId, postingId, userId, isDeleted) <> (RepliesRow.tupled, RepliesRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), reply, createdAt, numOfLike, numOfReply, imageUrl, parentId, Rep.Some(postingId), Rep.Some(userId), isDeleted).shaped.<>({r=>import r._; _1.map(_=> RepliesRow.tupled((_1.get, _2, _3, _4, _5, _6, _7, _8.get, _9.get, _10)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column reply SqlType(TEXT), Default(None) */
    val reply: Rep[Option[String]] = column[Option[String]]("reply", O.Default(None))
    /** Database column created_at SqlType(DATETIME), Default(None) */
    val createdAt: Rep[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("created_at", O.Default(None))
    /** Database column num_of_like SqlType(INT), Default(None) */
    val numOfLike: Rep[Option[Int]] = column[Option[Int]]("num_of_like", O.Default(None))
    /** Database column num_of_reply SqlType(INT), Default(None) */
    val numOfReply: Rep[Option[Int]] = column[Option[Int]]("num_of_reply", O.Default(None))
    /** Database column image_url SqlType(VARCHAR), Length(255,true), Default(None) */
    val imageUrl: Rep[Option[String]] = column[Option[String]]("image_url", O.Length(255,varying=true), O.Default(None))
    /** Database column parent_id SqlType(INT), Default(None) */
    val parentId: Rep[Option[Int]] = column[Option[Int]]("parent_id", O.Default(None))
    /** Database column posting_id SqlType(INT) */
    val postingId: Rep[Int] = column[Int]("posting_id")
    /** Database column user_id SqlType(INT) */
    val userId: Rep[Int] = column[Int]("user_id")
    /** Database column is_deleted SqlType(BIT), Default(None) */
    val isDeleted: Rep[Option[Boolean]] = column[Option[Boolean]]("is_deleted", O.Default(None))

    /** Foreign key referencing Postings (database name Replies_ibfk_1) */
    lazy val postingsFk = foreignKey("Replies_ibfk_1", postingId, Postings)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Users (database name Replies_ibfk_2) */
    lazy val usersFk = foreignKey("Replies_ibfk_2", userId, Users)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table Replies */
  lazy val Replies = new TableQuery(tag => new Replies(tag))

  /** Entity class storing rows of table ReplyLike
   *  @param replyId Database column reply_id SqlType(INT)
   *  @param userId Database column user_id SqlType(INT) */
  case class ReplyLikeRow(replyId: Int, userId: Int)
  /** GetResult implicit for fetching ReplyLikeRow objects using plain SQL queries */
  implicit def GetResultReplyLikeRow(implicit e0: GR[Int]): GR[ReplyLikeRow] = GR{
    prs => import prs._
    ReplyLikeRow.tupled((<<[Int], <<[Int]))
  }
  /** Table description of table Reply_like. Objects of this class serve as prototypes for rows in queries. */
  class ReplyLike(_tableTag: Tag) extends Table[ReplyLikeRow](_tableTag, "Reply_like") {
    def * = (replyId, userId) <> (ReplyLikeRow.tupled, ReplyLikeRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(replyId), Rep.Some(userId)).shaped.<>({r=>import r._; _1.map(_=> ReplyLikeRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column reply_id SqlType(INT) */
    val replyId: Rep[Int] = column[Int]("reply_id")
    /** Database column user_id SqlType(INT) */
    val userId: Rep[Int] = column[Int]("user_id")

    /** Foreign key referencing Replies (database name Reply_like_ibfk_1) */
    lazy val repliesFk = foreignKey("Reply_like_ibfk_1", replyId, Replies)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Users (database name Reply_like_ibfk_2) */
    lazy val usersFk = foreignKey("Reply_like_ibfk_2", userId, Users)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)

    /** Uniqueness Index over (replyId,userId) (database name reply_id) */
    val index1 = index("reply_id", (replyId, userId), unique=true)
  }
  /** Collection-like TableQuery object for table ReplyLike */
  lazy val ReplyLike = new TableQuery(tag => new ReplyLike(tag))

  /** Entity class storing rows of table ReplyReport
   *  @param replyId Database column reply_id SqlType(INT)
   *  @param userId Database column user_id SqlType(INT) */
  case class ReplyReportRow(replyId: Int, userId: Int)
  /** GetResult implicit for fetching ReplyReportRow objects using plain SQL queries */
  implicit def GetResultReplyReportRow(implicit e0: GR[Int]): GR[ReplyReportRow] = GR{
    prs => import prs._
    ReplyReportRow.tupled((<<[Int], <<[Int]))
  }
  /** Table description of table Reply_report. Objects of this class serve as prototypes for rows in queries. */
  class ReplyReport(_tableTag: Tag) extends Table[ReplyReportRow](_tableTag, "Reply_report") {
    def * = (replyId, userId) <> (ReplyReportRow.tupled, ReplyReportRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(replyId), Rep.Some(userId)).shaped.<>({r=>import r._; _1.map(_=> ReplyReportRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column reply_id SqlType(INT) */
    val replyId: Rep[Int] = column[Int]("reply_id")
    /** Database column user_id SqlType(INT) */
    val userId: Rep[Int] = column[Int]("user_id")

    /** Foreign key referencing Replies (database name Reply_report_ibfk_1) */
    lazy val repliesFk = foreignKey("Reply_report_ibfk_1", replyId, Replies)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Users (database name Reply_report_ibfk_2) */
    lazy val usersFk = foreignKey("Reply_report_ibfk_2", userId, Users)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)

    /** Uniqueness Index over (replyId,userId) (database name reply_id) */
    val index1 = index("reply_id", (replyId, userId), unique=true)
  }
  /** Collection-like TableQuery object for table ReplyReport */
  lazy val ReplyReport = new TableQuery(tag => new ReplyReport(tag))

  /** Entity class storing rows of table SaveHistory
    *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
    *  @param mentorId Database column mentor_id SqlType(INT), Default(None)
    *  @param postingId Database column posting_id SqlType(INT), Default(None)
    *  @param balance Database column balance SqlType(INT), Default(None)
    *  @param createdAt Database column created_at SqlType(DATE), Default(None)
    *  @param addedPoint Database column added_point SqlType(INT), Default(None) */
  case class SaveHistoryRow(id: Int, mentorId: Option[Int] = None, postingId: Option[Int] = None, balance: Option[Int] = None, createdAt: Option[java.sql.Date] = None, addedPoint: Option[Int] = None)
  /** GetResult implicit for fetching SaveHistoryRow objects using plain SQL queries */
  implicit def GetResultSaveHistoryRow(implicit e0: GR[Int], e1: GR[Option[Int]], e2: GR[Option[java.sql.Date]]): GR[SaveHistoryRow] = GR{
    prs => import prs._
      SaveHistoryRow.tupled((<<[Int], <<?[Int], <<?[Int], <<?[Int], <<?[java.sql.Date], <<?[Int]))
  }
  /** Table description of table Save_history. Objects of this class serve as prototypes for rows in queries. */
  class SaveHistory(_tableTag: Tag) extends Table[SaveHistoryRow](_tableTag, "Save_history") {
    def * = (id, mentorId, postingId, balance, createdAt, addedPoint) <> (SaveHistoryRow.tupled, SaveHistoryRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), mentorId, postingId, balance, createdAt, addedPoint).shaped.<>({r=>import r._; _1.map(_=> SaveHistoryRow.tupled((_1.get, _2, _3, _4, _5, _6)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column mentor_id SqlType(INT), Default(None) */
    val mentorId: Rep[Option[Int]] = column[Option[Int]]("mentor_id", O.Default(None))
    /** Database column posting_id SqlType(INT), Default(None) */
    val postingId: Rep[Option[Int]] = column[Option[Int]]("posting_id", O.Default(None))
    /** Database column balance SqlType(INT), Default(None) */
    val balance: Rep[Option[Int]] = column[Option[Int]]("balance", O.Default(None))
    /** Database column created_at SqlType(DATE), Default(None) */
    val createdAt: Rep[Option[java.sql.Date]] = column[Option[java.sql.Date]]("created_at", O.Default(None))
    /** Database column added_point SqlType(INT), Default(None) */
    val addedPoint: Rep[Option[Int]] = column[Option[Int]]("added_point", O.Default(None))

    /** Foreign key referencing Mentors (database name Save_history_ibfk_1) */
    lazy val mentorsFk = foreignKey("Save_history_ibfk_1", mentorId, Mentors)(r => Rep.Some(r.id), onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Postings (database name Save_history_ibfk_2) */
    lazy val postingsFk = foreignKey("Save_history_ibfk_2", postingId, Postings)(r => Rep.Some(r.id), onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table SaveHistory */
  lazy val SaveHistory = new TableQuery(tag => new SaveHistory(tag))

  /** Entity class storing rows of table SchoolExam
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param year Database column year SqlType(INT), Default(None)
   *  @param semester Database column semester SqlType(INT), Default(None)
   *  @param isActive Database column is_active SqlType(BIT), Default(None) */
  case class SchoolExamRow(id: Int, year: Option[Int] = None, semester: Option[Int] = None, isActive: Option[Boolean] = None)
  /** GetResult implicit for fetching SchoolExamRow objects using plain SQL queries */
  implicit def GetResultSchoolExamRow(implicit e0: GR[Int], e1: GR[Option[Int]], e2: GR[Option[Boolean]]): GR[SchoolExamRow] = GR{
    prs => import prs._
    SchoolExamRow.tupled((<<[Int], <<?[Int], <<?[Int], <<?[Boolean]))
  }
  /** Table description of table School_exam. Objects of this class serve as prototypes for rows in queries. */
  class SchoolExam(_tableTag: Tag) extends Table[SchoolExamRow](_tableTag, "School_exam") {
    def * = (id, year, semester, isActive) <> (SchoolExamRow.tupled, SchoolExamRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), year, semester, isActive).shaped.<>({r=>import r._; _1.map(_=> SchoolExamRow.tupled((_1.get, _2, _3, _4)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column year SqlType(INT), Default(None) */
    val year: Rep[Option[Int]] = column[Option[Int]]("year", O.Default(None))
    /** Database column semester SqlType(INT), Default(None) */
    val semester: Rep[Option[Int]] = column[Option[Int]]("semester", O.Default(None))
    /** Database column is_active SqlType(BIT), Default(None) */
    val isActive: Rep[Option[Boolean]] = column[Option[Boolean]]("is_active", O.Default(None))
  }
  /** Collection-like TableQuery object for table SchoolExam */
  lazy val SchoolExam = new TableQuery(tag => new SchoolExam(tag))

  /** Entity class storing rows of table SchoolExamRecord
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param schoolExamId Database column school_exam_id SqlType(INT)
   *  @param studentId Database column student_id SqlType(INT)
   *  @param korean Database column korean SqlType(INT), Default(None)
   *  @param math Database column math SqlType(INT), Default(None)
   *  @param english Database column english SqlType(INT), Default(None)
   *  @param social Database column social SqlType(INT), Default(None)
   *  @param science Database column science SqlType(INT), Default(None) */
  case class SchoolExamRecordRow(id: Int, schoolExamId: Int, studentId: Int, korean: Option[Int] = None, math: Option[Int] = None, english: Option[Int] = None, social: Option[Int] = None, science: Option[Int] = None)
  /** GetResult implicit for fetching SchoolExamRecordRow objects using plain SQL queries */
  implicit def GetResultSchoolExamRecordRow(implicit e0: GR[Int], e1: GR[Option[Int]]): GR[SchoolExamRecordRow] = GR{
    prs => import prs._
    SchoolExamRecordRow.tupled((<<[Int], <<[Int], <<[Int], <<?[Int], <<?[Int], <<?[Int], <<?[Int], <<?[Int]))
  }
  /** Table description of table School_exam_record. Objects of this class serve as prototypes for rows in queries. */
  class SchoolExamRecord(_tableTag: Tag) extends Table[SchoolExamRecordRow](_tableTag, "School_exam_record") {
    def * = (id, schoolExamId, studentId, korean, math, english, social, science) <> (SchoolExamRecordRow.tupled, SchoolExamRecordRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(schoolExamId), Rep.Some(studentId), korean, math, english, social, science).shaped.<>({r=>import r._; _1.map(_=> SchoolExamRecordRow.tupled((_1.get, _2.get, _3.get, _4, _5, _6, _7, _8)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column school_exam_id SqlType(INT) */
    val schoolExamId: Rep[Int] = column[Int]("school_exam_id")
    /** Database column student_id SqlType(INT) */
    val studentId: Rep[Int] = column[Int]("student_id")
    /** Database column korean SqlType(INT), Default(None) */
    val korean: Rep[Option[Int]] = column[Option[Int]]("korean", O.Default(None))
    /** Database column math SqlType(INT), Default(None) */
    val math: Rep[Option[Int]] = column[Option[Int]]("math", O.Default(None))
    /** Database column english SqlType(INT), Default(None) */
    val english: Rep[Option[Int]] = column[Option[Int]]("english", O.Default(None))
    /** Database column social SqlType(INT), Default(None) */
    val social: Rep[Option[Int]] = column[Option[Int]]("social", O.Default(None))
    /** Database column science SqlType(INT), Default(None) */
    val science: Rep[Option[Int]] = column[Option[Int]]("science", O.Default(None))

    /** Foreign key referencing SchoolExam (database name School_exam_record_ibfk_1) */
    lazy val schoolExamFk = foreignKey("School_exam_record_ibfk_1", schoolExamId, SchoolExam)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Students (database name School_exam_record_ibfk_2) */
    lazy val studentsFk = foreignKey("School_exam_record_ibfk_2", studentId, Students)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table SchoolExamRecord */
  lazy val SchoolExamRecord = new TableQuery(tag => new SchoolExamRecord(tag))

  /** Entity class storing rows of table SelectedTextbook
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param studentId Database column student_id SqlType(INT)
   *  @param studyType Database column study_type SqlType(INT)
   *  @param textbookId Database column textbook_id SqlType(INT)
   *  @param isCustom Database column is_custom SqlType(BIT), Default(None) */
  case class SelectedTextbookRow(id: Int, studentId: Int, studyType: Int, textbookId: Int, isCustom: Option[Boolean] = None)
  /** GetResult implicit for fetching SelectedTextbookRow objects using plain SQL queries */
  implicit def GetResultSelectedTextbookRow(implicit e0: GR[Int], e1: GR[Option[Boolean]]): GR[SelectedTextbookRow] = GR{
    prs => import prs._
    SelectedTextbookRow.tupled((<<[Int], <<[Int], <<[Int], <<[Int], <<?[Boolean]))
  }
  /** Table description of table Selected_textbook. Objects of this class serve as prototypes for rows in queries. */
  class SelectedTextbook(_tableTag: Tag) extends Table[SelectedTextbookRow](_tableTag, "Selected_textbook") {
    def * = (id, studentId, studyType, textbookId, isCustom) <> (SelectedTextbookRow.tupled, SelectedTextbookRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(studentId), Rep.Some(studyType), Rep.Some(textbookId), isCustom).shaped.<>({r=>import r._; _1.map(_=> SelectedTextbookRow.tupled((_1.get, _2.get, _3.get, _4.get, _5)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column student_id SqlType(INT) */
    val studentId: Rep[Int] = column[Int]("student_id")
    /** Database column study_type SqlType(INT) */
    val studyType: Rep[Int] = column[Int]("study_type")
    /** Database column textbook_id SqlType(INT) */
    val textbookId: Rep[Int] = column[Int]("textbook_id")
    /** Database column is_custom SqlType(BIT), Default(None) */
    val isCustom: Rep[Option[Boolean]] = column[Option[Boolean]]("is_custom", O.Default(None))

    /** Foreign key referencing Students (database name Selected_textbook_ibfk_1) */
    lazy val studentsFk = foreignKey("Selected_textbook_ibfk_1", studentId, Students)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing StudyType (database name Selected_textbook_ibfk_2) */
    lazy val studyTypeFk = foreignKey("Selected_textbook_ibfk_2", studyType, StudyType)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table SelectedTextbook */
  lazy val SelectedTextbook = new TableQuery(tag => new SelectedTextbook(tag))

  /** Entity class storing rows of table Students
   *  @param id Database column id SqlType(INT), PrimaryKey
   *  @param department Database column department SqlType(INT), Default(None)
   *  @param mathType Database column math_type SqlType(INT), Default(None)
   *  @param foreignLanguage Database column foreign_language SqlType(INT), Default(None)
   *  @param year Database column year SqlType(INT), Default(None)
   *  @param introduce Database column introduce SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param targetUniversity Database column target_university SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param targetDepartment Database column target_department SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param goal Database column goal SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param nickname Database column nickname SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param parentName Database column parent_name SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param parentPhone Database column parent_phone SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param mpEducation Database column mp_education SqlType(BIT), Default(None)
   *  @param totalgrade Database column totalGrade SqlType(INT), Default(Some(0)) */
  case class StudentsRow(id: Int, department: Option[Int] = None, mathType: Option[Int] = None, foreignLanguage: Option[Int] = None, year: Option[Int] = None, introduce: Option[String] = None, targetUniversity: Option[String] = None, targetDepartment: Option[String] = None, goal: Option[String] = None, nickname: Option[String] = None, parentName: Option[String] = None, parentPhone: Option[String] = None, mpEducation: Option[Boolean] = None, totalgrade: Option[Int] = Some(0))
  /** GetResult implicit for fetching StudentsRow objects using plain SQL queries */
  implicit def GetResultStudentsRow(implicit e0: GR[Int], e1: GR[Option[Int]], e2: GR[Option[String]], e3: GR[Option[Boolean]]): GR[StudentsRow] = GR{
    prs => import prs._
    StudentsRow.tupled((<<[Int], <<?[Int], <<?[Int], <<?[Int], <<?[Int], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[Boolean], <<?[Int]))
  }
  /** Table description of table Students. Objects of this class serve as prototypes for rows in queries. */
  class Students(_tableTag: Tag) extends Table[StudentsRow](_tableTag, "Students") {
    def * = (id, department, mathType, foreignLanguage, year, introduce, targetUniversity, targetDepartment, goal, nickname, parentName, parentPhone, mpEducation, totalgrade) <> (StudentsRow.tupled, StudentsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), department, mathType, foreignLanguage, year, introduce, targetUniversity, targetDepartment, goal, nickname, parentName, parentPhone, mpEducation, totalgrade).shaped.<>({r=>import r._; _1.map(_=> StudentsRow.tupled((_1.get, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.PrimaryKey)
    /** Database column department SqlType(INT), Default(None) */
    val department: Rep[Option[Int]] = column[Option[Int]]("department", O.Default(None))
    /** Database column math_type SqlType(INT), Default(None) */
    val mathType: Rep[Option[Int]] = column[Option[Int]]("math_type", O.Default(None))
    /** Database column foreign_language SqlType(INT), Default(None) */
    val foreignLanguage: Rep[Option[Int]] = column[Option[Int]]("foreign_language", O.Default(None))
    /** Database column year SqlType(INT), Default(None) */
    val year: Rep[Option[Int]] = column[Option[Int]]("year", O.Default(None))
    /** Database column introduce SqlType(VARCHAR), Length(255,true), Default(None) */
    val introduce: Rep[Option[String]] = column[Option[String]]("introduce", O.Length(255,varying=true), O.Default(None))
    /** Database column target_university SqlType(VARCHAR), Length(255,true), Default(None) */
    val targetUniversity: Rep[Option[String]] = column[Option[String]]("target_university", O.Length(255,varying=true), O.Default(None))
    /** Database column target_department SqlType(VARCHAR), Length(255,true), Default(None) */
    val targetDepartment: Rep[Option[String]] = column[Option[String]]("target_department", O.Length(255,varying=true), O.Default(None))
    /** Database column goal SqlType(VARCHAR), Length(255,true), Default(None) */
    val goal: Rep[Option[String]] = column[Option[String]]("goal", O.Length(255,varying=true), O.Default(None))
    /** Database column nickname SqlType(VARCHAR), Length(255,true), Default(None) */
    val nickname: Rep[Option[String]] = column[Option[String]]("nickname", O.Length(255,varying=true), O.Default(None))
    /** Database column parent_name SqlType(VARCHAR), Length(255,true), Default(None) */
    val parentName: Rep[Option[String]] = column[Option[String]]("parent_name", O.Length(255,varying=true), O.Default(None))
    /** Database column parent_phone SqlType(VARCHAR), Length(255,true), Default(None) */
    val parentPhone: Rep[Option[String]] = column[Option[String]]("parent_phone", O.Length(255,varying=true), O.Default(None))
    /** Database column mp_education SqlType(BIT), Default(None) */
    val mpEducation: Rep[Option[Boolean]] = column[Option[Boolean]]("mp_education", O.Default(None))
    /** Database column totalGrade SqlType(INT), Default(Some(0)) */
    val totalgrade: Rep[Option[Int]] = column[Option[Int]]("totalGrade", O.Default(Some(0)))

    /** Foreign key referencing Users (database name Students_ibfk_1) */
    lazy val usersFk = foreignKey("Students_ibfk_1", id, Users)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table Students */
  lazy val Students = new TableQuery(tag => new Students(tag))

  /** Entity class storing rows of table Studies
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param createdAt Database column created_at SqlType(DATE), Default(None)
   *  @param goal Database column goal SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param isRepeated Database column is_repeated SqlType(BIT), Default(None)
   *  @param textbookId Database column textbook_id SqlType(INT), Default(None)
   *  @param studentId Database column student_id SqlType(INT)
   *  @param isDeleted Database column is_deleted SqlType(BIT), Default(None)
   *  @param lectureId Database column lecture_id SqlType(INT), Default(None)
   *  @param activityTypeId Database column activity_type_id SqlType(INT)
   *  @param studyTypeId Database column study_type_id SqlType(INT)
   *  @param customTextbookId Database column custom_textbook_id SqlType(INT), Default(None) */
  case class StudiesRow(id: Int, createdAt: Option[java.sql.Date] = None, goal: Option[String] = None, isRepeated: Option[Boolean] = None, textbookId: Option[Int] = None, studentId: Int, isDeleted: Option[Boolean] = None, lectureId: Option[Int] = None, activityTypeId: Int, studyTypeId: Int, customTextbookId: Option[Int] = None)
  /** GetResult implicit for fetching StudiesRow objects using plain SQL queries */
  implicit def GetResultStudiesRow(implicit e0: GR[Int], e1: GR[Option[java.sql.Date]], e2: GR[Option[String]], e3: GR[Option[Boolean]], e4: GR[Option[Int]]): GR[StudiesRow] = GR{
    prs => import prs._
    StudiesRow.tupled((<<[Int], <<?[java.sql.Date], <<?[String], <<?[Boolean], <<?[Int], <<[Int], <<?[Boolean], <<?[Int], <<[Int], <<[Int], <<?[Int]))
  }
  /** Table description of table Studies. Objects of this class serve as prototypes for rows in queries. */
  class Studies(_tableTag: Tag) extends Table[StudiesRow](_tableTag, "Studies") {
    def * = (id, createdAt, goal, isRepeated, textbookId, studentId, isDeleted, lectureId, activityTypeId, studyTypeId, customTextbookId) <> (StudiesRow.tupled, StudiesRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), createdAt, goal, isRepeated, textbookId, Rep.Some(studentId), isDeleted, lectureId, Rep.Some(activityTypeId), Rep.Some(studyTypeId), customTextbookId).shaped.<>({r=>import r._; _1.map(_=> StudiesRow.tupled((_1.get, _2, _3, _4, _5, _6.get, _7, _8, _9.get, _10.get, _11)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column created_at SqlType(DATE), Default(None) */
    val createdAt: Rep[Option[java.sql.Date]] = column[Option[java.sql.Date]]("created_at", O.Default(None))
    /** Database column goal SqlType(VARCHAR), Length(255,true), Default(None) */
    val goal: Rep[Option[String]] = column[Option[String]]("goal", O.Length(255,varying=true), O.Default(None))
    /** Database column is_repeated SqlType(BIT), Default(None) */
    val isRepeated: Rep[Option[Boolean]] = column[Option[Boolean]]("is_repeated", O.Default(None))
    /** Database column textbook_id SqlType(INT), Default(None) */
    val textbookId: Rep[Option[Int]] = column[Option[Int]]("textbook_id", O.Default(None))
    /** Database column student_id SqlType(INT) */
    val studentId: Rep[Int] = column[Int]("student_id")
    /** Database column is_deleted SqlType(BIT), Default(None) */
    val isDeleted: Rep[Option[Boolean]] = column[Option[Boolean]]("is_deleted", O.Default(None))
    /** Database column lecture_id SqlType(INT), Default(None) */
    val lectureId: Rep[Option[Int]] = column[Option[Int]]("lecture_id", O.Default(None))
    /** Database column activity_type_id SqlType(INT) */
    val activityTypeId: Rep[Int] = column[Int]("activity_type_id")
    /** Database column study_type_id SqlType(INT) */
    val studyTypeId: Rep[Int] = column[Int]("study_type_id")
    /** Database column custom_textbook_id SqlType(INT), Default(None) */
    val customTextbookId: Rep[Option[Int]] = column[Option[Int]]("custom_textbook_id", O.Default(None))

    /** Foreign key referencing ActivityType (database name Studies_ibfk_3) */
    lazy val activityTypeFk = foreignKey("Studies_ibfk_3", activityTypeId, ActivityType)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Students (database name Studies_ibfk_1) */
    lazy val studentsFk = foreignKey("Studies_ibfk_1", studentId, Students)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing StudyType (database name Studies_ibfk_6) */
    lazy val studyTypeFk = foreignKey("Studies_ibfk_6", studyTypeId, StudyType)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)

    /** Index over (lectureId) (database name Studies_ibfk_4) */
    val index1 = index("Studies_ibfk_4", lectureId)
    /** Index over (textbookId) (database name textbook_id) */
    val index2 = index("textbook_id", textbookId)
  }
  /** Collection-like TableQuery object for table Studies */
  lazy val Studies = new TableQuery(tag => new Studies(tag))

  /** Entity class storing rows of table StudyGoal
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param monday Database column monday SqlType(DATE), Default(None)
   *  @param goalTime Database column goal_time SqlType(BIGINT), Default(None)
   *  @param alarm Database column alarm SqlType(BIT), Default(None)
   *  @param studentId Database column student_id SqlType(INT), Default(None) */
  case class StudyGoalRow(id: Int, monday: Option[java.sql.Date] = None, goalTime: Option[Long] = None, alarm: Option[Boolean] = None, studentId: Option[Int] = None)
  /** GetResult implicit for fetching StudyGoalRow objects using plain SQL queries */
  implicit def GetResultStudyGoalRow(implicit e0: GR[Int], e1: GR[Option[java.sql.Date]], e2: GR[Option[Long]], e3: GR[Option[Boolean]], e4: GR[Option[Int]]): GR[StudyGoalRow] = GR{
    prs => import prs._
    StudyGoalRow.tupled((<<[Int], <<?[java.sql.Date], <<?[Long], <<?[Boolean], <<?[Int]))
  }
  /** Table description of table Study_goal. Objects of this class serve as prototypes for rows in queries. */
  class StudyGoal(_tableTag: Tag) extends Table[StudyGoalRow](_tableTag, "Study_goal") {
    def * = (id, monday, goalTime, alarm, studentId) <> (StudyGoalRow.tupled, StudyGoalRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), monday, goalTime, alarm, studentId).shaped.<>({r=>import r._; _1.map(_=> StudyGoalRow.tupled((_1.get, _2, _3, _4, _5)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column monday SqlType(DATE), Default(None) */
    val monday: Rep[Option[java.sql.Date]] = column[Option[java.sql.Date]]("monday", O.Default(None))
    /** Database column goal_time SqlType(BIGINT), Default(None) */
    val goalTime: Rep[Option[Long]] = column[Option[Long]]("goal_time", O.Default(None))
    /** Database column alarm SqlType(BIT), Default(None) */
    val alarm: Rep[Option[Boolean]] = column[Option[Boolean]]("alarm", O.Default(None))
    /** Database column student_id SqlType(INT), Default(None) */
    val studentId: Rep[Option[Int]] = column[Option[Int]]("student_id", O.Default(None))

    /** Foreign key referencing Students (database name Study_goal_ibfk_1) */
    lazy val studentsFk = foreignKey("Study_goal_ibfk_1", studentId, Students)(r => Rep.Some(r.id), onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table StudyGoal */
  lazy val StudyGoal = new TableQuery(tag => new StudyGoal(tag))

  /** Entity class storing rows of table StudyRecords
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param createdAt Database column created_at SqlType(DATETIME), Default(None)
   *  @param startTime Database column start_time SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param endTime Database column end_time SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param duration Database column duration SqlType(BIGINT), Default(None)
   *  @param studyId Database column study_id SqlType(INT)
   *  @param startPage Database column start_page SqlType(INT), Default(None)
   *  @param endPage Database column end_page SqlType(INT), Default(None) */
  case class StudyRecordsRow(id: Int, createdAt: Option[java.sql.Timestamp] = None, startTime: Option[String] = None, endTime: Option[String] = None, duration: Option[Long] = None, studyId: Int, startPage: Option[Int] = None, endPage: Option[Int] = None)
  /** GetResult implicit for fetching StudyRecordsRow objects using plain SQL queries */
  implicit def GetResultStudyRecordsRow(implicit e0: GR[Int], e1: GR[Option[java.sql.Timestamp]], e2: GR[Option[String]], e3: GR[Option[Long]], e4: GR[Option[Int]]): GR[StudyRecordsRow] = GR{
    prs => import prs._
    StudyRecordsRow.tupled((<<[Int], <<?[java.sql.Timestamp], <<?[String], <<?[String], <<?[Long], <<[Int], <<?[Int], <<?[Int]))
  }
  /** Table description of table Study_records. Objects of this class serve as prototypes for rows in queries. */
  class StudyRecords(_tableTag: Tag) extends Table[StudyRecordsRow](_tableTag, "Study_records") {
    def * = (id, createdAt, startTime, endTime, duration, studyId, startPage, endPage) <> (StudyRecordsRow.tupled, StudyRecordsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), createdAt, startTime, endTime, duration, Rep.Some(studyId), startPage, endPage).shaped.<>({r=>import r._; _1.map(_=> StudyRecordsRow.tupled((_1.get, _2, _3, _4, _5, _6.get, _7, _8)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column created_at SqlType(DATETIME), Default(None) */
    val createdAt: Rep[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("created_at", O.Default(None))
    /** Database column start_time SqlType(VARCHAR), Length(255,true), Default(None) */
    val startTime: Rep[Option[String]] = column[Option[String]]("start_time", O.Length(255,varying=true), O.Default(None))
    /** Database column end_time SqlType(VARCHAR), Length(255,true), Default(None) */
    val endTime: Rep[Option[String]] = column[Option[String]]("end_time", O.Length(255,varying=true), O.Default(None))
    /** Database column duration SqlType(BIGINT), Default(None) */
    val duration: Rep[Option[Long]] = column[Option[Long]]("duration", O.Default(None))
    /** Database column study_id SqlType(INT) */
    val studyId: Rep[Int] = column[Int]("study_id")
    /** Database column start_page SqlType(INT), Default(None) */
    val startPage: Rep[Option[Int]] = column[Option[Int]]("start_page", O.Default(None))
    /** Database column end_page SqlType(INT), Default(None) */
    val endPage: Rep[Option[Int]] = column[Option[Int]]("end_page", O.Default(None))

    /** Foreign key referencing Studies (database name Study_records_ibfk_1) */
    lazy val studiesFk = foreignKey("Study_records_ibfk_1", studyId, Studies)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table StudyRecords */
  lazy val StudyRecords = new TableQuery(tag => new StudyRecords(tag))

  /** Entity class storing rows of table StudyTime
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param createdAt Database column created_at SqlType(DATE), Default(None)
   *  @param totalStudy Database column total_study SqlType(BIGINT), Default(None)
   *  @param subject Database column subject SqlType(BIGINT), Default(None)
   *  @param nonsubject Database column nonsubject SqlType(BIGINT), Default(None)
   *  @param alone Database column alone SqlType(BIGINT), Default(None)
   *  @param korea Database column korea SqlType(BIGINT), Default(None)
   *  @param math Database column math SqlType(BIGINT), Default(None)
   *  @param english Database column english SqlType(BIGINT), Default(None)
   *  @param socialScience Database column social_science SqlType(BIGINT), Default(None)
   *  @param sleep Database column sleep SqlType(BIGINT), Default(None)
   *  @param eat Database column eat SqlType(BIGINT), Default(None)
   *  @param rest Database column rest SqlType(BIGINT), Default(None)
   *  @param etc Database column etc SqlType(BIGINT), Default(None)
   *  @param waste Database column waste SqlType(BIGINT), Default(None)
   *  @param studentId Database column student_id SqlType(INT), Default(None) */
  case class StudyTimeRow(id: Int, createdAt: Option[java.sql.Date] = None, totalStudy: Option[Long] = None, subject: Option[Long] = None, nonsubject: Option[Long] = None, alone: Option[Long] = None, korea: Option[Long] = None, math: Option[Long] = None, english: Option[Long] = None, socialScience: Option[Long] = None, sleep: Option[Long] = None, eat: Option[Long] = None, rest: Option[Long] = None, etc: Option[Long] = None, waste: Option[Long] = None, studentId: Option[Int] = None)
  /** GetResult implicit for fetching StudyTimeRow objects using plain SQL queries */
  implicit def GetResultStudyTimeRow(implicit e0: GR[Int], e1: GR[Option[java.sql.Date]], e2: GR[Option[Long]], e3: GR[Option[Int]]): GR[StudyTimeRow] = GR{
    prs => import prs._
    StudyTimeRow.tupled((<<[Int], <<?[java.sql.Date], <<?[Long], <<?[Long], <<?[Long], <<?[Long], <<?[Long], <<?[Long], <<?[Long], <<?[Long], <<?[Long], <<?[Long], <<?[Long], <<?[Long], <<?[Long], <<?[Int]))
  }
  /** Table description of table study_time. Objects of this class serve as prototypes for rows in queries. */
  class StudyTime(_tableTag: Tag) extends Table[StudyTimeRow](_tableTag, "study_time") {
    def * = (id, createdAt, totalStudy, subject, nonsubject, alone, korea, math, english, socialScience, sleep, eat, rest, etc, waste, studentId) <> (StudyTimeRow.tupled, StudyTimeRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), createdAt, totalStudy, subject, nonsubject, alone, korea, math, english, socialScience, sleep, eat, rest, etc, waste, studentId).shaped.<>({r=>import r._; _1.map(_=> StudyTimeRow.tupled((_1.get, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column created_at SqlType(DATE), Default(None) */
    val createdAt: Rep[Option[java.sql.Date]] = column[Option[java.sql.Date]]("created_at", O.Default(None))
    /** Database column total_study SqlType(BIGINT), Default(None) */
    val totalStudy: Rep[Option[Long]] = column[Option[Long]]("total_study", O.Default(None))
    /** Database column subject SqlType(BIGINT), Default(None) */
    val subject: Rep[Option[Long]] = column[Option[Long]]("subject", O.Default(None))
    /** Database column nonsubject SqlType(BIGINT), Default(None) */
    val nonsubject: Rep[Option[Long]] = column[Option[Long]]("nonsubject", O.Default(None))
    /** Database column alone SqlType(BIGINT), Default(None) */
    val alone: Rep[Option[Long]] = column[Option[Long]]("alone", O.Default(None))
    /** Database column korea SqlType(BIGINT), Default(None) */
    val korea: Rep[Option[Long]] = column[Option[Long]]("korea", O.Default(None))
    /** Database column math SqlType(BIGINT), Default(None) */
    val math: Rep[Option[Long]] = column[Option[Long]]("math", O.Default(None))
    /** Database column english SqlType(BIGINT), Default(None) */
    val english: Rep[Option[Long]] = column[Option[Long]]("english", O.Default(None))
    /** Database column social_science SqlType(BIGINT), Default(None) */
    val socialScience: Rep[Option[Long]] = column[Option[Long]]("social_science", O.Default(None))
    /** Database column sleep SqlType(BIGINT), Default(None) */
    val sleep: Rep[Option[Long]] = column[Option[Long]]("sleep", O.Default(None))
    /** Database column eat SqlType(BIGINT), Default(None) */
    val eat: Rep[Option[Long]] = column[Option[Long]]("eat", O.Default(None))
    /** Database column rest SqlType(BIGINT), Default(None) */
    val rest: Rep[Option[Long]] = column[Option[Long]]("rest", O.Default(None))
    /** Database column etc SqlType(BIGINT), Default(None) */
    val etc: Rep[Option[Long]] = column[Option[Long]]("etc", O.Default(None))
    /** Database column waste SqlType(BIGINT), Default(None) */
    val waste: Rep[Option[Long]] = column[Option[Long]]("waste", O.Default(None))
    /** Database column student_id SqlType(INT), Default(None) */
    val studentId: Rep[Option[Int]] = column[Option[Int]]("student_id", O.Default(None))

    /** Foreign key referencing Students (database name FK_study_time_1) */
    lazy val studentsFk = foreignKey("FK_study_time_1", studentId, Students)(r => Rep.Some(r.id), onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table StudyTime */
  lazy val StudyTime = new TableQuery(tag => new StudyTime(tag))

  /** Entity class storing rows of table StudyTimeSubject
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param studentId Database column student_id SqlType(INT), Default(None)
   *  @param subjectId Database column subject_id SqlType(INT)
   *  @param contentsBasic Database column contents_basic SqlType(BIGINT), Default(None)
   *  @param contentsSolution Database column contents_solution SqlType(BIGINT), Default(None)
   *  @param contentsEbs Database column contents_ebs SqlType(BIGINT), Default(None)
   *  @param contentsReal Database column contents_real SqlType(BIGINT), Default(None)
   *  @param typeIndependently Database column type_independently SqlType(BIGINT), Default(None)
   *  @param typeLecture Database column type_lecture SqlType(BIGINT), Default(None)
   *  @param typeSchool Database column type_school SqlType(BIGINT), Default(None)
   *  @param typePrivateEdu Database column type_private_edu SqlType(BIGINT), Default(None)
   *  @param typePrivateTeacher Database column type_private_teacher SqlType(BIGINT), Default(None)
   *  @param typeCheck Database column type_check SqlType(BIGINT), Default(None)
   *  @param typeExam Database column type_exam SqlType(BIGINT), Default(None)
   *  @param createdAt Database column created_at SqlType(DATE), Default(None)
   *  @param title Database column title SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param typeId Database column type_id SqlType(INT), Default(None)
   *  @param totalStudy Database column total_study SqlType(BIGINT), Default(None) */
  case class StudyTimeSubjectRow(id: Int, studentId: Option[Int] = None, subjectId: Int, contentsBasic: Option[Long] = None, contentsSolution: Option[Long] = None, contentsEbs: Option[Long] = None, contentsReal: Option[Long] = None, typeIndependently: Option[Long] = None, typeLecture: Option[Long] = None, typeSchool: Option[Long] = None, typePrivateEdu: Option[Long] = None, typePrivateTeacher: Option[Long] = None, typeCheck: Option[Long] = None, typeExam: Option[Long] = None, createdAt: Option[java.sql.Date] = None, title: Option[String] = None, typeId: Option[Int] = None, totalStudy: Option[Long] = None)
  /** GetResult implicit for fetching StudyTimeSubjectRow objects using plain SQL queries */
  implicit def GetResultStudyTimeSubjectRow(implicit e0: GR[Int], e1: GR[Option[Int]], e2: GR[Option[Long]], e3: GR[Option[java.sql.Date]], e4: GR[Option[String]]): GR[StudyTimeSubjectRow] = GR{
    prs => import prs._
    StudyTimeSubjectRow.tupled((<<[Int], <<?[Int], <<[Int], <<?[Long], <<?[Long], <<?[Long], <<?[Long], <<?[Long], <<?[Long], <<?[Long], <<?[Long], <<?[Long], <<?[Long], <<?[Long], <<?[java.sql.Date], <<?[String], <<?[Int], <<?[Long]))
  }
  /** Table description of table Study_time_subject. Objects of this class serve as prototypes for rows in queries. */
  class StudyTimeSubject(_tableTag: Tag) extends Table[StudyTimeSubjectRow](_tableTag, "Study_time_subject") {
    def * = (id, studentId, subjectId, contentsBasic, contentsSolution, contentsEbs, contentsReal, typeIndependently, typeLecture, typeSchool, typePrivateEdu, typePrivateTeacher, typeCheck, typeExam, createdAt, title, typeId, totalStudy) <> (StudyTimeSubjectRow.tupled, StudyTimeSubjectRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), studentId, Rep.Some(subjectId), contentsBasic, contentsSolution, contentsEbs, contentsReal, typeIndependently, typeLecture, typeSchool, typePrivateEdu, typePrivateTeacher, typeCheck, typeExam, createdAt, title, typeId, totalStudy).shaped.<>({r=>import r._; _1.map(_=> StudyTimeSubjectRow.tupled((_1.get, _2, _3.get, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16, _17, _18)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column student_id SqlType(INT), Default(None) */
    val studentId: Rep[Option[Int]] = column[Option[Int]]("student_id", O.Default(None))
    /** Database column subject_id SqlType(INT) */
    val subjectId: Rep[Int] = column[Int]("subject_id")
    /** Database column contents_basic SqlType(BIGINT), Default(None) */
    val contentsBasic: Rep[Option[Long]] = column[Option[Long]]("contents_basic", O.Default(None))
    /** Database column contents_solution SqlType(BIGINT), Default(None) */
    val contentsSolution: Rep[Option[Long]] = column[Option[Long]]("contents_solution", O.Default(None))
    /** Database column contents_ebs SqlType(BIGINT), Default(None) */
    val contentsEbs: Rep[Option[Long]] = column[Option[Long]]("contents_ebs", O.Default(None))
    /** Database column contents_real SqlType(BIGINT), Default(None) */
    val contentsReal: Rep[Option[Long]] = column[Option[Long]]("contents_real", O.Default(None))
    /** Database column type_independently SqlType(BIGINT), Default(None) */
    val typeIndependently: Rep[Option[Long]] = column[Option[Long]]("type_independently", O.Default(None))
    /** Database column type_lecture SqlType(BIGINT), Default(None) */
    val typeLecture: Rep[Option[Long]] = column[Option[Long]]("type_lecture", O.Default(None))
    /** Database column type_school SqlType(BIGINT), Default(None) */
    val typeSchool: Rep[Option[Long]] = column[Option[Long]]("type_school", O.Default(None))
    /** Database column type_private_edu SqlType(BIGINT), Default(None) */
    val typePrivateEdu: Rep[Option[Long]] = column[Option[Long]]("type_private_edu", O.Default(None))
    /** Database column type_private_teacher SqlType(BIGINT), Default(None) */
    val typePrivateTeacher: Rep[Option[Long]] = column[Option[Long]]("type_private_teacher", O.Default(None))
    /** Database column type_check SqlType(BIGINT), Default(None) */
    val typeCheck: Rep[Option[Long]] = column[Option[Long]]("type_check", O.Default(None))
    /** Database column type_exam SqlType(BIGINT), Default(None) */
    val typeExam: Rep[Option[Long]] = column[Option[Long]]("type_exam", O.Default(None))
    /** Database column created_at SqlType(DATE), Default(None) */
    val createdAt: Rep[Option[java.sql.Date]] = column[Option[java.sql.Date]]("created_at", O.Default(None))
    /** Database column title SqlType(VARCHAR), Length(255,true), Default(None) */
    val title: Rep[Option[String]] = column[Option[String]]("title", O.Length(255,varying=true), O.Default(None))
    /** Database column type_id SqlType(INT), Default(None) */
    val typeId: Rep[Option[Int]] = column[Option[Int]]("type_id", O.Default(None))
    /** Database column total_study SqlType(BIGINT), Default(None) */
    val totalStudy: Rep[Option[Long]] = column[Option[Long]]("total_study", O.Default(None))

    /** Foreign key referencing Students (database name Study_time_subject_ibfk_1) */
    lazy val studentsFk = foreignKey("Study_time_subject_ibfk_1", studentId, Students)(r => Rep.Some(r.id), onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Subjects (database name Study_time_subject_ibfk_2) */
    lazy val subjectsFk = foreignKey("Study_time_subject_ibfk_2", subjectId, Subjects)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table StudyTimeSubject */
  lazy val StudyTimeSubject = new TableQuery(tag => new StudyTimeSubject(tag))

  /** Entity class storing rows of table StudyTimeSubSubject
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param studyTimeSubjectId Database column study_time_subject_id SqlType(INT), Default(None)
   *  @param subjectId Database column subject_id SqlType(INT), Default(None)
   *  @param time Database column time SqlType(BIGINT), Default(None) */
  case class StudyTimeSubSubjectRow(id: Int, studyTimeSubjectId: Option[Int] = None, subjectId: Option[Int] = None, time: Option[Long] = None)
  /** GetResult implicit for fetching StudyTimeSubSubjectRow objects using plain SQL queries */
  implicit def GetResultStudyTimeSubSubjectRow(implicit e0: GR[Int], e1: GR[Option[Int]], e2: GR[Option[Long]]): GR[StudyTimeSubSubjectRow] = GR{
    prs => import prs._
    StudyTimeSubSubjectRow.tupled((<<[Int], <<?[Int], <<?[Int], <<?[Long]))
  }
  /** Table description of table Study_time_sub_subject. Objects of this class serve as prototypes for rows in queries. */
  class StudyTimeSubSubject(_tableTag: Tag) extends Table[StudyTimeSubSubjectRow](_tableTag, "Study_time_sub_subject") {
    def * = (id, studyTimeSubjectId, subjectId, time) <> (StudyTimeSubSubjectRow.tupled, StudyTimeSubSubjectRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), studyTimeSubjectId, subjectId, time).shaped.<>({r=>import r._; _1.map(_=> StudyTimeSubSubjectRow.tupled((_1.get, _2, _3, _4)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column study_time_subject_id SqlType(INT), Default(None) */
    val studyTimeSubjectId: Rep[Option[Int]] = column[Option[Int]]("study_time_subject_id", O.Default(None))
    /** Database column subject_id SqlType(INT), Default(None) */
    val subjectId: Rep[Option[Int]] = column[Option[Int]]("subject_id", O.Default(None))
    /** Database column time SqlType(BIGINT), Default(None) */
    val time: Rep[Option[Long]] = column[Option[Long]]("time", O.Default(None))

    /** Foreign key referencing StudyTimeSubject (database name study_time_sub_subject_ibfk_1) */
    lazy val studyTimeSubjectFk = foreignKey("study_time_sub_subject_ibfk_1", studyTimeSubjectId, StudyTimeSubject)(r => Rep.Some(r.id), onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)

    /** Index over (subjectId) (database name study_time_sub_subject_ibfk_2_idx) */
    val index1 = index("study_time_sub_subject_ibfk_2_idx", subjectId)
  }
  /** Collection-like TableQuery object for table StudyTimeSubSubject */
  lazy val StudyTimeSubSubject = new TableQuery(tag => new StudyTimeSubSubject(tag))

  /** Entity class storing rows of table StudyType
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param title Database column title SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param isStudy Database column is_study SqlType(BIT), Default(None) */
  case class StudyTypeRow(id: Int, title: Option[String] = None, isStudy: Option[Boolean] = None)
  /** GetResult implicit for fetching StudyTypeRow objects using plain SQL queries */
  implicit def GetResultStudyTypeRow(implicit e0: GR[Int], e1: GR[Option[String]], e2: GR[Option[Boolean]]): GR[StudyTypeRow] = GR{
    prs => import prs._
    StudyTypeRow.tupled((<<[Int], <<?[String], <<?[Boolean]))
  }
  /** Table description of table Study_type. Objects of this class serve as prototypes for rows in queries. */
  class StudyType(_tableTag: Tag) extends Table[StudyTypeRow](_tableTag, "Study_type") {
    def * = (id, title, isStudy) <> (StudyTypeRow.tupled, StudyTypeRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), title, isStudy).shaped.<>({r=>import r._; _1.map(_=> StudyTypeRow.tupled((_1.get, _2, _3)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column title SqlType(VARCHAR), Length(255,true), Default(None) */
    val title: Rep[Option[String]] = column[Option[String]]("title", O.Length(255,varying=true), O.Default(None))
    /** Database column is_study SqlType(BIT), Default(None) */
    val isStudy: Rep[Option[Boolean]] = column[Option[Boolean]]("is_study", O.Default(None))
  }
  /** Collection-like TableQuery object for table StudyType */
  lazy val StudyType = new TableQuery(tag => new StudyType(tag))

  /** Entity class storing rows of table Subjects
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param title Database column title SqlType(TEXT), Default(None)
   *  @param depth1 Database column depth1 SqlType(INT), Default(None)
   *  @param depth2 Database column depth2 SqlType(INT), Default(None)
   *  @param depth3 Database column depth3 SqlType(INT), Default(None)
   *  @param examTitle Database column exam_title SqlType(VARCHAR), Length(255,true), Default(None) */
  case class SubjectsRow(id: Int, title: Option[String] = None, depth1: Option[Int] = None, depth2: Option[Int] = None, depth3: Option[Int] = None, examTitle: Option[String] = None)
  /** GetResult implicit for fetching SubjectsRow objects using plain SQL queries */
  implicit def GetResultSubjectsRow(implicit e0: GR[Int], e1: GR[Option[String]], e2: GR[Option[Int]]): GR[SubjectsRow] = GR{
    prs => import prs._
    SubjectsRow.tupled((<<[Int], <<?[String], <<?[Int], <<?[Int], <<?[Int], <<?[String]))
  }
  /** Table description of table Subjects. Objects of this class serve as prototypes for rows in queries. */
  class Subjects(_tableTag: Tag) extends Table[SubjectsRow](_tableTag, "Subjects") {
    def * = (id, title, depth1, depth2, depth3, examTitle) <> (SubjectsRow.tupled, SubjectsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), title, depth1, depth2, depth3, examTitle).shaped.<>({r=>import r._; _1.map(_=> SubjectsRow.tupled((_1.get, _2, _3, _4, _5, _6)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column title SqlType(TEXT), Default(None) */
    val title: Rep[Option[String]] = column[Option[String]]("title", O.Default(None))
    /** Database column depth1 SqlType(INT), Default(None) */
    val depth1: Rep[Option[Int]] = column[Option[Int]]("depth1", O.Default(None))
    /** Database column depth2 SqlType(INT), Default(None) */
    val depth2: Rep[Option[Int]] = column[Option[Int]]("depth2", O.Default(None))
    /** Database column depth3 SqlType(INT), Default(None) */
    val depth3: Rep[Option[Int]] = column[Option[Int]]("depth3", O.Default(None))
    /** Database column exam_title SqlType(VARCHAR), Length(255,true), Default(None) */
    val examTitle: Rep[Option[String]] = column[Option[String]]("exam_title", O.Length(255,varying=true), O.Default(None))
  }
  /** Collection-like TableQuery object for table Subjects */
  lazy val Subjects = new TableQuery(tag => new Subjects(tag))

  /** Entity class storing rows of table TargetDepartments
   *  @param departmentId Database column department_id SqlType(INT)
   *  @param studentId Database column student_id SqlType(INT) */
  case class TargetDepartmentsRow(departmentId: Int, studentId: Int)
  /** GetResult implicit for fetching TargetDepartmentsRow objects using plain SQL queries */
  implicit def GetResultTargetDepartmentsRow(implicit e0: GR[Int]): GR[TargetDepartmentsRow] = GR{
    prs => import prs._
    TargetDepartmentsRow.tupled((<<[Int], <<[Int]))
  }
  /** Table description of table Target_departments. Objects of this class serve as prototypes for rows in queries. */
  class TargetDepartments(_tableTag: Tag) extends Table[TargetDepartmentsRow](_tableTag, "Target_departments") {
    def * = (departmentId, studentId) <> (TargetDepartmentsRow.tupled, TargetDepartmentsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(departmentId), Rep.Some(studentId)).shaped.<>({r=>import r._; _1.map(_=> TargetDepartmentsRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column department_id SqlType(INT) */
    val departmentId: Rep[Int] = column[Int]("department_id")
    /** Database column student_id SqlType(INT) */
    val studentId: Rep[Int] = column[Int]("student_id")

    /** Foreign key referencing Departments (database name Target_departments_ibfk_1) */
    lazy val departmentsFk = foreignKey("Target_departments_ibfk_1", departmentId, Departments)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Students (database name Target_departments_ibfk_2) */
    lazy val studentsFk = foreignKey("Target_departments_ibfk_2", studentId, Students)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table TargetDepartments */
  lazy val TargetDepartments = new TableQuery(tag => new TargetDepartments(tag))

  /** Entity class storing rows of table Teachers
   *  @param id Database column id SqlType(INT), PrimaryKey
   *  @param classification Database column classification SqlType(VARCHAR), Length(32,true), Default(None)
   *  @param birthday Database column birthday SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param lastSchool Database column last_school SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param isGraduated Database column is_graduated SqlType(BIT), Default(None)
   *  @param isAuthenticated Database column is_authenticated SqlType(BIT), Default(Some(false)) */
  case class TeachersRow(id: Int, classification: Option[String] = None, birthday: Option[String] = None, lastSchool: Option[String] = None, isGraduated: Option[Boolean] = None, isAuthenticated: Option[Boolean] = Some(false))
  /** GetResult implicit for fetching TeachersRow objects using plain SQL queries */
  implicit def GetResultTeachersRow(implicit e0: GR[Int], e1: GR[Option[String]], e2: GR[Option[Boolean]]): GR[TeachersRow] = GR{
    prs => import prs._
    TeachersRow.tupled((<<[Int], <<?[String], <<?[String], <<?[String], <<?[Boolean], <<?[Boolean]))
  }
  /** Table description of table Teachers. Objects of this class serve as prototypes for rows in queries. */
  class Teachers(_tableTag: Tag) extends Table[TeachersRow](_tableTag, "Teachers") {
    def * = (id, classification, birthday, lastSchool, isGraduated, isAuthenticated) <> (TeachersRow.tupled, TeachersRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), classification, birthday, lastSchool, isGraduated, isAuthenticated).shaped.<>({r=>import r._; _1.map(_=> TeachersRow.tupled((_1.get, _2, _3, _4, _5, _6)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.PrimaryKey)
    /** Database column classification SqlType(VARCHAR), Length(32,true), Default(None) */
    val classification: Rep[Option[String]] = column[Option[String]]("classification", O.Length(32,varying=true), O.Default(None))
    /** Database column birthday SqlType(VARCHAR), Length(255,true), Default(None) */
    val birthday: Rep[Option[String]] = column[Option[String]]("birthday", O.Length(255,varying=true), O.Default(None))
    /** Database column last_school SqlType(VARCHAR), Length(255,true), Default(None) */
    val lastSchool: Rep[Option[String]] = column[Option[String]]("last_school", O.Length(255,varying=true), O.Default(None))
    /** Database column is_graduated SqlType(BIT), Default(None) */
    val isGraduated: Rep[Option[Boolean]] = column[Option[Boolean]]("is_graduated", O.Default(None))
    /** Database column is_authenticated SqlType(BIT), Default(Some(false)) */
    val isAuthenticated: Rep[Option[Boolean]] = column[Option[Boolean]]("is_authenticated", O.Default(Some(false)))

    /** Foreign key referencing Users (database name Teachers_ibfk_1) */
    lazy val usersFk = foreignKey("Teachers_ibfk_1", id, Users)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table Teachers */
  lazy val Teachers = new TableQuery(tag => new Teachers(tag))

  /** Entity class storing rows of table Textbooks
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param originTitle Database column origin_title SqlType(TEXT), Default(None)
   *  @param title Database column title SqlType(TEXT), Default(None)
   *  @param author Database column author SqlType(VARCHAR), Length(255,true), Default(None)
   *  @param issueDate Database column issue_date SqlType(INT), Default(None)
   *  @param subjectId Database column subject_id SqlType(INT)
   *  @param analysisCategoryId Database column analysis_category_id SqlType(INT)
   *  @param association Database column association SqlType(INT), Default(None) */
  case class TextbooksRow(id: Int, originTitle: Option[String] = None, title: Option[String] = None, author: Option[String] = None, issueDate: Option[Int] = None, subjectId: Int, analysisCategoryId: Int, association: Option[Int] = None)
  /** GetResult implicit for fetching TextbooksRow objects using plain SQL queries */
  implicit def GetResultTextbooksRow(implicit e0: GR[Int], e1: GR[Option[String]], e2: GR[Option[Int]]): GR[TextbooksRow] = GR{
    prs => import prs._
    TextbooksRow.tupled((<<[Int], <<?[String], <<?[String], <<?[String], <<?[Int], <<[Int], <<[Int], <<?[Int]))
  }
  /** Table description of table Textbooks. Objects of this class serve as prototypes for rows in queries. */
  class Textbooks(_tableTag: Tag) extends Table[TextbooksRow](_tableTag, "Textbooks") {
    def * = (id, originTitle, title, author, issueDate, subjectId, analysisCategoryId, association) <> (TextbooksRow.tupled, TextbooksRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), originTitle, title, author, issueDate, Rep.Some(subjectId), Rep.Some(analysisCategoryId), association).shaped.<>({r=>import r._; _1.map(_=> TextbooksRow.tupled((_1.get, _2, _3, _4, _5, _6.get, _7.get, _8)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column origin_title SqlType(TEXT), Default(None) */
    val originTitle: Rep[Option[String]] = column[Option[String]]("origin_title", O.Default(None))
    /** Database column title SqlType(TEXT), Default(None) */
    val title: Rep[Option[String]] = column[Option[String]]("title", O.Default(None))
    /** Database column author SqlType(VARCHAR), Length(255,true), Default(None) */
    val author: Rep[Option[String]] = column[Option[String]]("author", O.Length(255,varying=true), O.Default(None))
    /** Database column issue_date SqlType(INT), Default(None) */
    val issueDate: Rep[Option[Int]] = column[Option[Int]]("issue_date", O.Default(None))
    /** Database column subject_id SqlType(INT) */
    val subjectId: Rep[Int] = column[Int]("subject_id")
    /** Database column analysis_category_id SqlType(INT) */
    val analysisCategoryId: Rep[Int] = column[Int]("analysis_category_id")
    /** Database column association SqlType(INT), Default(None) */
    val association: Rep[Option[Int]] = column[Option[Int]]("association", O.Default(None))

    /** Foreign key referencing AnalysisCategory (database name Textbooks_ibfk_1) */
    lazy val analysisCategoryFk = foreignKey("Textbooks_ibfk_1", analysisCategoryId, AnalysisCategory)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Subjects (database name Textbooks_ibfk_2) */
    lazy val subjectsFk = foreignKey("Textbooks_ibfk_2", subjectId, Subjects)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table Textbooks */
  lazy val Textbooks = new TableQuery(tag => new Textbooks(tag))

  /** Entity class storing rows of table TSMatching
   *  @param teacherId Database column teacher_id SqlType(INT)
   *  @param studentId Database column student_id SqlType(INT)
   *  @param isApproval Database column is_approval SqlType(BIT), Default(None)
   *  @param isAuthenticated Database column is_Authenticated SqlType(INT), Default(Some(0))
   *  @param createdAt Database column created_at SqlType(DATETIME), Default(None) */
  case class TSMatchingRow(teacherId: Int, studentId: Int, isApproval: Option[Boolean] = None, isAuthenticated: Option[Int] = Some(0), createdAt: Option[java.sql.Timestamp] = None)
  /** GetResult implicit for fetching TSMatchingRow objects using plain SQL queries */
  implicit def GetResultTSMatchingRow(implicit e0: GR[Int], e1: GR[Option[Boolean]], e2: GR[Option[Int]], e3: GR[Option[java.sql.Timestamp]]): GR[TSMatchingRow] = GR{
    prs => import prs._
    TSMatchingRow.tupled((<<[Int], <<[Int], <<?[Boolean], <<?[Int], <<?[java.sql.Timestamp]))
  }
  /** Table description of table T_S_Matching. Objects of this class serve as prototypes for rows in queries. */
  class TSMatching(_tableTag: Tag) extends Table[TSMatchingRow](_tableTag, "T_S_Matching") {
    def * = (teacherId, studentId, isApproval, isAuthenticated, createdAt) <> (TSMatchingRow.tupled, TSMatchingRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(teacherId), Rep.Some(studentId), isApproval, isAuthenticated, createdAt).shaped.<>({r=>import r._; _1.map(_=> TSMatchingRow.tupled((_1.get, _2.get, _3, _4, _5)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column teacher_id SqlType(INT) */
    val teacherId: Rep[Int] = column[Int]("teacher_id")
    /** Database column student_id SqlType(INT) */
    val studentId: Rep[Int] = column[Int]("student_id")
    /** Database column is_approval SqlType(BIT), Default(None) */
    val isApproval: Rep[Option[Boolean]] = column[Option[Boolean]]("is_approval", O.Default(None))
    /** Database column is_Authenticated SqlType(INT), Default(Some(0)) */
    val isAuthenticated: Rep[Option[Int]] = column[Option[Int]]("is_Authenticated", O.Default(Some(0)))
    /** Database column created_at SqlType(DATETIME), Default(None) */
    val createdAt: Rep[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("created_at", O.Default(None))

    /** Foreign key referencing Students (database name T_S_Matching_ibfk_1) */
    lazy val studentsFk = foreignKey("T_S_Matching_ibfk_1", studentId, Students)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Teachers (database name T_S_Matching_ibfk_2) */
    lazy val teachersFk = foreignKey("T_S_Matching_ibfk_2", teacherId, Teachers)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)

    /** Uniqueness Index over (teacherId,studentId) (database name teacher_id) */
    val index1 = index("teacher_id", (teacherId, studentId), unique=true)
  }
  /** Collection-like TableQuery object for table TSMatching */
  lazy val TSMatching = new TableQuery(tag => new TSMatching(tag))

  /** Entity class storing rows of table UserEdu
   *  @param eduInstId Database column edu_inst_id SqlType(INT)
   *  @param userId Database column user_id SqlType(INT)
   *  @param isDefault Database column is_default SqlType(BIT), Default(None) */
  case class UserEduRow(eduInstId: Int, userId: Int, isDefault: Option[Boolean] = None)
  /** GetResult implicit for fetching UserEduRow objects using plain SQL queries */
  implicit def GetResultUserEduRow(implicit e0: GR[Int], e1: GR[Option[Boolean]]): GR[UserEduRow] = GR{
    prs => import prs._
    UserEduRow.tupled((<<[Int], <<[Int], <<?[Boolean]))
  }
  /** Table description of table User_edu. Objects of this class serve as prototypes for rows in queries. */
  class UserEdu(_tableTag: Tag) extends Table[UserEduRow](_tableTag, "User_edu") {
    def * = (eduInstId, userId, isDefault) <> (UserEduRow.tupled, UserEduRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(eduInstId), Rep.Some(userId), isDefault).shaped.<>({r=>import r._; _1.map(_=> UserEduRow.tupled((_1.get, _2.get, _3)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column edu_inst_id SqlType(INT) */
    val eduInstId: Rep[Int] = column[Int]("edu_inst_id")
    /** Database column user_id SqlType(INT) */
    val userId: Rep[Int] = column[Int]("user_id")
    /** Database column is_default SqlType(BIT), Default(None) */
    val isDefault: Rep[Option[Boolean]] = column[Option[Boolean]]("is_default", O.Default(None))

    /** Foreign key referencing EducationalInst (database name User_edu_ibfk_1) */
    lazy val educationalInstFk = foreignKey("User_edu_ibfk_1", eduInstId, EducationalInst)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Users (database name User_edu_ibfk_2) */
    lazy val usersFk = foreignKey("User_edu_ibfk_2", userId, Users)(r => r.id, onUpdate=ForeignKeyAction.Cascade, onDelete=ForeignKeyAction.Cascade)
  }
  /** Collection-like TableQuery object for table UserEdu */
  lazy val UserEdu = new TableQuery(tag => new UserEdu(tag))

  /** Entity class storing rows of table Users
    *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
    *  @param name Database column name SqlType(VARCHAR), Length(255,true), Default(None)
    *  @param email Database column email SqlType(VARCHAR), Length(255,true), Default(None)
    *  @param password Database column password SqlType(VARCHAR), Length(255,true), Default(None)
    *  @param phone Database column phone SqlType(VARCHAR), Length(255,true), Default(None)
    *  @param gender Database column gender SqlType(VARCHAR), Length(32,true), Default(None)
    *  @param profileImage Database column profile_image SqlType(VARCHAR), Length(255,true), Default(None)
    *  @param secret Database column secret SqlType(VARCHAR), Length(255,true), Default(None)
    *  @param typeId Database column type_id SqlType(INT)
    *  @param isWithdrawal Database column is_withdrawal SqlType(BIT), Default(None)
    *  @param push Database column push SqlType(BIT), Default(Some(true))
    *  @param typeT Database column type_t SqlType(INT), Default(None)
    *  @param createdAt Database column created_at SqlType(DATETIME), Default(None)
    *  @param token Database column token SqlType(VARCHAR), Length(255,true), Default(None)
    *  @param isAuthenticated Database column is_authenticated SqlType(INT), Default(Some(0))
    *  @param withdrawalApproval Database column withdrawal_approval SqlType(BIT), Default(Some(false)) */
  case class UsersRow(id: Int, name: Option[String] = None, email: Option[String] = None, password: Option[String] = None, phone: Option[String] = None, gender: Option[String] = None, profileImage: Option[String] = None, secret: Option[String] = None, typeId: Int, isWithdrawal: Option[Boolean] = None, push: Option[Boolean] = Some(true), typeT: Option[Int] = None, createdAt: Option[java.sql.Timestamp] = None, token: Option[String] = None, isAuthenticated: Option[Int] = Some(0), withdrawalApproval: Option[Boolean] = Some(false))
  /** GetResult implicit for fetching UsersRow objects using plain SQL queries */
  implicit def GetResultUsersRow(implicit e0: GR[Int], e1: GR[Option[String]], e2: GR[Option[Boolean]], e3: GR[Option[Int]], e4: GR[Option[java.sql.Timestamp]]): GR[UsersRow] = GR{
    prs => import prs._
      UsersRow.tupled((<<[Int], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<?[String], <<[Int], <<?[Boolean], <<?[Boolean], <<?[Int], <<?[java.sql.Timestamp], <<?[String], <<?[Int], <<?[Boolean]))
  }
  /** Table description of table Users. Objects of this class serve as prototypes for rows in queries. */
  class Users(_tableTag: Tag) extends Table[UsersRow](_tableTag, "Users") {
    def * = (id, name, email, password, phone, gender, profileImage, secret, typeId, isWithdrawal, push, typeT, createdAt, token, isAuthenticated, withdrawalApproval) <> (UsersRow.tupled, UsersRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), name, email, password, phone, gender, profileImage, secret, Rep.Some(typeId), isWithdrawal, push, typeT, createdAt, token, isAuthenticated, withdrawalApproval).shaped.<>({r=>import r._; _1.map(_=> UsersRow.tupled((_1.get, _2, _3, _4, _5, _6, _7, _8, _9.get, _10, _11, _12, _13, _14, _15, _16)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column name SqlType(VARCHAR), Length(255,true), Default(None) */
    val name: Rep[Option[String]] = column[Option[String]]("name", O.Length(255,varying=true), O.Default(None))
    /** Database column email SqlType(VARCHAR), Length(255,true), Default(None) */
    val email: Rep[Option[String]] = column[Option[String]]("email", O.Length(255,varying=true), O.Default(None))
    /** Database column password SqlType(VARCHAR), Length(255,true), Default(None) */
    val password: Rep[Option[String]] = column[Option[String]]("password", O.Length(255,varying=true), O.Default(None))
    /** Database column phone SqlType(VARCHAR), Length(255,true), Default(None) */
    val phone: Rep[Option[String]] = column[Option[String]]("phone", O.Length(255,varying=true), O.Default(None))
    /** Database column gender SqlType(VARCHAR), Length(32,true), Default(None) */
    val gender: Rep[Option[String]] = column[Option[String]]("gender", O.Length(32,varying=true), O.Default(None))
    /** Database column profile_image SqlType(VARCHAR), Length(255,true), Default(None) */
    val profileImage: Rep[Option[String]] = column[Option[String]]("profile_image", O.Length(255,varying=true), O.Default(None))
    /** Database column secret SqlType(VARCHAR), Length(255,true), Default(None) */
    val secret: Rep[Option[String]] = column[Option[String]]("secret", O.Length(255,varying=true), O.Default(None))
    /** Database column type_id SqlType(INT) */
    val typeId: Rep[Int] = column[Int]("type_id")
    /** Database column is_withdrawal SqlType(BIT), Default(None) */
    val isWithdrawal: Rep[Option[Boolean]] = column[Option[Boolean]]("is_withdrawal", O.Default(None))
    /** Database column push SqlType(BIT), Default(Some(true)) */
    val push: Rep[Option[Boolean]] = column[Option[Boolean]]("push", O.Default(Some(true)))
    /** Database column type_t SqlType(INT), Default(None) */
    val typeT: Rep[Option[Int]] = column[Option[Int]]("type_t", O.Default(None))
    /** Database column created_at SqlType(DATETIME), Default(None) */
    val createdAt: Rep[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("created_at", O.Default(None))
    /** Database column token SqlType(VARCHAR), Length(255,true), Default(None) */
    val token: Rep[Option[String]] = column[Option[String]]("token", O.Length(255,varying=true), O.Default(None))
    /** Database column is_authenticated SqlType(INT), Default(Some(0)) */
    val isAuthenticated: Rep[Option[Int]] = column[Option[Int]]("is_authenticated", O.Default(Some(0)))
    /** Database column withdrawal_approval SqlType(BIT), Default(Some(false)) */
    val withdrawalApproval: Rep[Option[Boolean]] = column[Option[Boolean]]("withdrawal_approval", O.Default(Some(false)))

    /** Foreign key referencing UserType (database name Users_ibfk_1) */
    lazy val userTypeFk = foreignKey("Users_ibfk_1", typeId, UserType)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table Users */
  lazy val Users = new TableQuery(tag => new Users(tag))

  /** Entity class storing rows of table UserType
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param `type` Database column type SqlType(VARCHAR), Length(255,true), Default(None) */
  case class UserTypeRow(id: Int, `type`: Option[String] = None)
  /** GetResult implicit for fetching UserTypeRow objects using plain SQL queries */
  implicit def GetResultUserTypeRow(implicit e0: GR[Int], e1: GR[Option[String]]): GR[UserTypeRow] = GR{
    prs => import prs._
    UserTypeRow.tupled((<<[Int], <<?[String]))
  }
  /** Table description of table User_type. Objects of this class serve as prototypes for rows in queries.
   *  NOTE: The following names collided with Scala keywords and were escaped: type */
  class UserType(_tableTag: Tag) extends Table[UserTypeRow](_tableTag, "User_type") {
    def * = (id, `type`) <> (UserTypeRow.tupled, UserTypeRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), `type`).shaped.<>({r=>import r._; _1.map(_=> UserTypeRow.tupled((_1.get, _2)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column type SqlType(VARCHAR), Length(255,true), Default(None)
     *  NOTE: The name was escaped because it collided with a Scala keyword. */
    val `type`: Rep[Option[String]] = column[Option[String]]("type", O.Length(255,varying=true), O.Default(None))
  }
  /** Collection-like TableQuery object for table UserType */
  lazy val UserType = new TableQuery(tag => new UserType(tag))



}
