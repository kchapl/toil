package model

sealed trait Category { def code: String }

case object Income extends Category {
  override val code = "I"
}

case object Spend extends Category {
  override val code = "S"
}

case object Transfer extends Category {
  override val code = "T"
}

case object Repayment extends Category {
  override val code = "R"
}

case object Refund extends Category {
  override val code = "B"
}

case object Uncategorised extends Category {
  override val code = "U"
}

object Category {
  val all = Seq(Income, Spend, Transfer, Repayment, Refund, Uncategorised)

  def fromCode(code: String) = code match {
    case Income.`code` => Income
    case Spend.`code` => Spend
    case Transfer.`code` => Transfer
    case Repayment.`code` => Repayment
    case Refund.`code` => Refund
    case Uncategorised.`code` => Uncategorised
  }
}