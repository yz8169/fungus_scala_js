package models
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = slick.jdbc.MySQLProfile
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: slick.jdbc.JdbcProfile
  import profile.api._
  import com.github.tototoshi.slick.MySQLJodaSupport._
  import org.joda.time.DateTime
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = Account.schema ++ Appendix.schema ++ Basicinfo.schema ++ Classify.schema ++ Submission.schema
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table Account
   *  @param id Database column id SqlType(INT), PrimaryKey
   *  @param account Database column account SqlType(VARCHAR), Length(255,true)
   *  @param password Database column password SqlType(VARCHAR), Length(255,true) */
  case class AccountRow(id: Int, account: String, password: String)
  /** GetResult implicit for fetching AccountRow objects using plain SQL queries */
  implicit def GetResultAccountRow(implicit e0: GR[Int], e1: GR[String]): GR[AccountRow] = GR{
    prs => import prs._
    AccountRow.tupled((<<[Int], <<[String], <<[String]))
  }
  /** Table description of table account. Objects of this class serve as prototypes for rows in queries. */
  class Account(_tableTag: Tag) extends profile.api.Table[AccountRow](_tableTag, Some("fungus"), "account") {
    def * = (id, account, password) <> (AccountRow.tupled, AccountRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(account), Rep.Some(password)).shaped.<>({r=>import r._; _1.map(_=> AccountRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.PrimaryKey)
    /** Database column account SqlType(VARCHAR), Length(255,true) */
    val account: Rep[String] = column[String]("account", O.Length(255,varying=true))
    /** Database column password SqlType(VARCHAR), Length(255,true) */
    val password: Rep[String] = column[String]("password", O.Length(255,varying=true))
  }
  /** Collection-like TableQuery object for table Account */
  lazy val Account = new TableQuery(tag => new Account(tag))

  /** Entity class storing rows of table Appendix
   *  @param id Database column id SqlType(VARCHAR), PrimaryKey, Length(255,true)
   *  @param images Database column images SqlType(VARCHAR), Length(255,true) */
  case class AppendixRow(id: String, images: String)
  /** GetResult implicit for fetching AppendixRow objects using plain SQL queries */
  implicit def GetResultAppendixRow(implicit e0: GR[String]): GR[AppendixRow] = GR{
    prs => import prs._
    AppendixRow.tupled((<<[String], <<[String]))
  }
  /** Table description of table appendix. Objects of this class serve as prototypes for rows in queries. */
  class Appendix(_tableTag: Tag) extends profile.api.Table[AppendixRow](_tableTag, Some("fungus"), "appendix") {
    def * = (id, images) <> (AppendixRow.tupled, AppendixRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(images)).shaped.<>({r=>import r._; _1.map(_=> AppendixRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(VARCHAR), PrimaryKey, Length(255,true) */
    val id: Rep[String] = column[String]("id", O.PrimaryKey, O.Length(255,varying=true))
    /** Database column images SqlType(VARCHAR), Length(255,true) */
    val images: Rep[String] = column[String]("images", O.Length(255,varying=true))
  }
  /** Collection-like TableQuery object for table Appendix */
  lazy val Appendix = new TableQuery(tag => new Appendix(tag))

  /** Entity class storing rows of table Basicinfo
   *  @param id Database column id SqlType(VARCHAR), PrimaryKey, Length(255,true)
   *  @param originalid Database column originalId SqlType(VARCHAR), Length(255,true)
   *  @param chinesename Database column chineseName SqlType(VARCHAR), Length(255,true)
   *  @param source Database column source SqlType(VARCHAR), Length(255,true)
   *  @param storetime Database column storeTime SqlType(VARCHAR), Length(255,true)
   *  @param opcountry Database column oPCountry SqlType(VARCHAR), Length(255,true)
   *  @param habitat Database column habitat SqlType(VARCHAR), Length(255,true)
   *  @param ctemperature Database column cTemperature SqlType(VARCHAR), Length(255,true)
   *  @param cnumber Database column cNumber SqlType(VARCHAR), Length(255,true)
   *  @param mcharacteristic Database column mCharacteristic SqlType(VARCHAR), Length(255,true)
   *  @param pkind Database column pKind SqlType(VARCHAR), Length(255,true)
   *  @param pmethod Database column pMethod SqlType(VARCHAR), Length(255,true)
   *  @param bhlevel Database column bHLevel SqlType(VARCHAR), Length(255,true)
   *  @param pstate Database column pState SqlType(VARCHAR), Length(255,true)
   *  @param its Database column its SqlType(TEXT)
   *  @param eighteens Database column eighteenS SqlType(TEXT)
   *  @param price Database column price SqlType(INT)
   *  @param inventory Database column inventory SqlType(INT)
   *  @param shareMode Database column share_mode SqlType(TEXT) */
  case class BasicinfoRow(id: String, originalid: String, chinesename: String, source: String, storetime: String, opcountry: String, habitat: String, ctemperature: String, cnumber: String, mcharacteristic: String, pkind: String, pmethod: String, bhlevel: String, pstate: String, its: String, eighteens: String, price: Int, inventory: Int, shareMode: String)
  /** GetResult implicit for fetching BasicinfoRow objects using plain SQL queries */
  implicit def GetResultBasicinfoRow(implicit e0: GR[String], e1: GR[Int]): GR[BasicinfoRow] = GR{
    prs => import prs._
    BasicinfoRow.tupled((<<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[Int], <<[Int], <<[String]))
  }
  /** Table description of table basicinfo. Objects of this class serve as prototypes for rows in queries. */
  class Basicinfo(_tableTag: Tag) extends profile.api.Table[BasicinfoRow](_tableTag, Some("fungus"), "basicinfo") {
    def * = (id, originalid, chinesename, source, storetime, opcountry, habitat, ctemperature, cnumber, mcharacteristic, pkind, pmethod, bhlevel, pstate, its, eighteens, price, inventory, shareMode) <> (BasicinfoRow.tupled, BasicinfoRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(originalid), Rep.Some(chinesename), Rep.Some(source), Rep.Some(storetime), Rep.Some(opcountry), Rep.Some(habitat), Rep.Some(ctemperature), Rep.Some(cnumber), Rep.Some(mcharacteristic), Rep.Some(pkind), Rep.Some(pmethod), Rep.Some(bhlevel), Rep.Some(pstate), Rep.Some(its), Rep.Some(eighteens), Rep.Some(price), Rep.Some(inventory), Rep.Some(shareMode)).shaped.<>({r=>import r._; _1.map(_=> BasicinfoRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get, _10.get, _11.get, _12.get, _13.get, _14.get, _15.get, _16.get, _17.get, _18.get, _19.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(VARCHAR), PrimaryKey, Length(255,true) */
    val id: Rep[String] = column[String]("id", O.PrimaryKey, O.Length(255,varying=true))
    /** Database column originalId SqlType(VARCHAR), Length(255,true) */
    val originalid: Rep[String] = column[String]("originalId", O.Length(255,varying=true))
    /** Database column chineseName SqlType(VARCHAR), Length(255,true) */
    val chinesename: Rep[String] = column[String]("chineseName", O.Length(255,varying=true))
    /** Database column source SqlType(VARCHAR), Length(255,true) */
    val source: Rep[String] = column[String]("source", O.Length(255,varying=true))
    /** Database column storeTime SqlType(VARCHAR), Length(255,true) */
    val storetime: Rep[String] = column[String]("storeTime", O.Length(255,varying=true))
    /** Database column oPCountry SqlType(VARCHAR), Length(255,true) */
    val opcountry: Rep[String] = column[String]("oPCountry", O.Length(255,varying=true))
    /** Database column habitat SqlType(VARCHAR), Length(255,true) */
    val habitat: Rep[String] = column[String]("habitat", O.Length(255,varying=true))
    /** Database column cTemperature SqlType(VARCHAR), Length(255,true) */
    val ctemperature: Rep[String] = column[String]("cTemperature", O.Length(255,varying=true))
    /** Database column cNumber SqlType(VARCHAR), Length(255,true) */
    val cnumber: Rep[String] = column[String]("cNumber", O.Length(255,varying=true))
    /** Database column mCharacteristic SqlType(VARCHAR), Length(255,true) */
    val mcharacteristic: Rep[String] = column[String]("mCharacteristic", O.Length(255,varying=true))
    /** Database column pKind SqlType(VARCHAR), Length(255,true) */
    val pkind: Rep[String] = column[String]("pKind", O.Length(255,varying=true))
    /** Database column pMethod SqlType(VARCHAR), Length(255,true) */
    val pmethod: Rep[String] = column[String]("pMethod", O.Length(255,varying=true))
    /** Database column bHLevel SqlType(VARCHAR), Length(255,true) */
    val bhlevel: Rep[String] = column[String]("bHLevel", O.Length(255,varying=true))
    /** Database column pState SqlType(VARCHAR), Length(255,true) */
    val pstate: Rep[String] = column[String]("pState", O.Length(255,varying=true))
    /** Database column its SqlType(TEXT) */
    val its: Rep[String] = column[String]("its")
    /** Database column eighteenS SqlType(TEXT) */
    val eighteens: Rep[String] = column[String]("eighteenS")
    /** Database column price SqlType(INT) */
    val price: Rep[Int] = column[Int]("price")
    /** Database column inventory SqlType(INT) */
    val inventory: Rep[Int] = column[Int]("inventory")
    /** Database column share_mode SqlType(TEXT) */
    val shareMode: Rep[String] = column[String]("share_mode")
  }
  /** Collection-like TableQuery object for table Basicinfo */
  lazy val Basicinfo = new TableQuery(tag => new Basicinfo(tag))

  /** Entity class storing rows of table Classify
   *  @param id Database column id SqlType(VARCHAR), PrimaryKey, Length(255,true)
   *  @param phylum Database column phylum SqlType(VARCHAR), Length(255,true)
   *  @param outline Database column outline SqlType(VARCHAR), Length(255,true)
   *  @param order Database column order SqlType(VARCHAR), Length(255,true)
   *  @param family Database column family SqlType(VARCHAR), Length(255,true)
   *  @param genus Database column genus SqlType(VARCHAR), Length(255,true)
   *  @param species Database column species SqlType(VARCHAR), Length(255,true) */
  case class ClassifyRow(id: String, phylum: String, outline: String, order: String, family: String, genus: String, species: String)
  /** GetResult implicit for fetching ClassifyRow objects using plain SQL queries */
  implicit def GetResultClassifyRow(implicit e0: GR[String]): GR[ClassifyRow] = GR{
    prs => import prs._
    ClassifyRow.tupled((<<[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[String]))
  }
  /** Table description of table classify. Objects of this class serve as prototypes for rows in queries. */
  class Classify(_tableTag: Tag) extends profile.api.Table[ClassifyRow](_tableTag, Some("fungus"), "classify") {
    def * = (id, phylum, outline, order, family, genus, species) <> (ClassifyRow.tupled, ClassifyRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(phylum), Rep.Some(outline), Rep.Some(order), Rep.Some(family), Rep.Some(genus), Rep.Some(species)).shaped.<>({r=>import r._; _1.map(_=> ClassifyRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(VARCHAR), PrimaryKey, Length(255,true) */
    val id: Rep[String] = column[String]("id", O.PrimaryKey, O.Length(255,varying=true))
    /** Database column phylum SqlType(VARCHAR), Length(255,true) */
    val phylum: Rep[String] = column[String]("phylum", O.Length(255,varying=true))
    /** Database column outline SqlType(VARCHAR), Length(255,true) */
    val outline: Rep[String] = column[String]("outline", O.Length(255,varying=true))
    /** Database column order SqlType(VARCHAR), Length(255,true) */
    val order: Rep[String] = column[String]("order", O.Length(255,varying=true))
    /** Database column family SqlType(VARCHAR), Length(255,true) */
    val family: Rep[String] = column[String]("family", O.Length(255,varying=true))
    /** Database column genus SqlType(VARCHAR), Length(255,true) */
    val genus: Rep[String] = column[String]("genus", O.Length(255,varying=true))
    /** Database column species SqlType(VARCHAR), Length(255,true) */
    val species: Rep[String] = column[String]("species", O.Length(255,varying=true))
  }
  /** Collection-like TableQuery object for table Classify */
  lazy val Classify = new TableQuery(tag => new Classify(tag))

  /** Entity class storing rows of table Submission
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param name Database column name SqlType(VARCHAR), Length(255,true)
   *  @param department Database column department SqlType(VARCHAR), Length(255,true)
   *  @param phone Database column phone SqlType(VARCHAR), Length(255,true)
   *  @param email Database column email SqlType(VARCHAR), Length(255,true)
   *  @param content Database column content SqlType(VARCHAR), Length(255,true)
   *  @param time Database column time SqlType(DATETIME)
   *  @param address Database column address SqlType(VARCHAR), Length(255,true)
   *  @param isdeal Database column isDeal SqlType(INT)
   *  @param totalprice Database column totalPrice SqlType(INT) */
  case class SubmissionRow(id: Int, name: String, department: String, phone: String, email: String, content: String, time: DateTime, address: String, isdeal: Int, totalprice: Int)
  /** GetResult implicit for fetching SubmissionRow objects using plain SQL queries */
  implicit def GetResultSubmissionRow(implicit e0: GR[Int], e1: GR[String], e2: GR[DateTime]): GR[SubmissionRow] = GR{
    prs => import prs._
    SubmissionRow.tupled((<<[Int], <<[String], <<[String], <<[String], <<[String], <<[String], <<[DateTime], <<[String], <<[Int], <<[Int]))
  }
  /** Table description of table submission. Objects of this class serve as prototypes for rows in queries. */
  class Submission(_tableTag: Tag) extends profile.api.Table[SubmissionRow](_tableTag, Some("fungus"), "submission") {
    def * = (id, name, department, phone, email, content, time, address, isdeal, totalprice) <> (SubmissionRow.tupled, SubmissionRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(name), Rep.Some(department), Rep.Some(phone), Rep.Some(email), Rep.Some(content), Rep.Some(time), Rep.Some(address), Rep.Some(isdeal), Rep.Some(totalprice)).shaped.<>({r=>import r._; _1.map(_=> SubmissionRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get, _10.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column name SqlType(VARCHAR), Length(255,true) */
    val name: Rep[String] = column[String]("name", O.Length(255,varying=true))
    /** Database column department SqlType(VARCHAR), Length(255,true) */
    val department: Rep[String] = column[String]("department", O.Length(255,varying=true))
    /** Database column phone SqlType(VARCHAR), Length(255,true) */
    val phone: Rep[String] = column[String]("phone", O.Length(255,varying=true))
    /** Database column email SqlType(VARCHAR), Length(255,true) */
    val email: Rep[String] = column[String]("email", O.Length(255,varying=true))
    /** Database column content SqlType(VARCHAR), Length(255,true) */
    val content: Rep[String] = column[String]("content", O.Length(255,varying=true))
    /** Database column time SqlType(DATETIME) */
    val time: Rep[DateTime] = column[DateTime]("time")
    /** Database column address SqlType(VARCHAR), Length(255,true) */
    val address: Rep[String] = column[String]("address", O.Length(255,varying=true))
    /** Database column isDeal SqlType(INT) */
    val isdeal: Rep[Int] = column[Int]("isDeal")
    /** Database column totalPrice SqlType(INT) */
    val totalprice: Rep[Int] = column[Int]("totalPrice")
  }
  /** Collection-like TableQuery object for table Submission */
  lazy val Submission = new TableQuery(tag => new Submission(tag))
}
