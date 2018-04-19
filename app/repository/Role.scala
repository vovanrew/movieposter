package repository


trait Role {
  val name: String
}

object Role {

  class Admin extends Role {
    val name = "admin"
  }

  implicit def role2String: Role => String = role => role.name

  implicit def string2Role: String => Role = str => new Role { val name = str }

}