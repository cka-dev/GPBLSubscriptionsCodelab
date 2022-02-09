package com.sample.basicscodelab.data

import android.location.Address

data class UserDataModel(
    private var id: Int? = null,
    private var name: String? = null,
    private var username: String? = null,
    private var email: String? = null,
    private var address: Address? = null,
    private var phone: String? = null,
    private var website: String? = null,
    private var company: String? = null
) {

}