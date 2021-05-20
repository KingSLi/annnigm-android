package edu.phystech.annnigm

data class User(var firstName: String? = null,
                var lastName: String? = null,
                var email: String? = null,
                var phone: String? = null,
                var sex: String? = null,
                var birthDate: String? = null,
                var height: Int = 0,
                var weight: Double = 0.0)

val User.isNotEmpty: Boolean
    get() {
        return (firstName != null)
                && lastName != null
                && email != null
                && sex != null
                && birthDate != null
    }