package noprofit.foss.NOSQL

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class Transaction(
    @Id var id: Long = 0L,
    var name: String = "",
    var amount: Double = 0.0,
    var note: String? = "",
    var date_created: Long = 0,
    var date_time_modified: Long? = 0,
    var original_date_due: Long? = 0,
    var income: Long? = 0,//cashew boolean but keep the type
    var paid: Long = 0,//cashew boolean but keep the type
    var skipPaid: Long? = 0,//cashew boolean but keep the type
    var uuid: String? = ""  //var optcashewuuid: String? = null// Nullable UUID
)
