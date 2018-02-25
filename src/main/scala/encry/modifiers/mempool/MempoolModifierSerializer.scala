package encry.modifiers.mempool

import scorex.core.serialization.Serializer

import scala.util.{Failure, Try}

object MempoolModifierSerializer extends Serializer[EncryBaseTransaction] {

  override def toBytes(obj: EncryBaseTransaction): Array[Byte] = obj match {
    case m: CoinbaseTransaction =>
      CoinbaseTransaction.typeId +: CoinbaseTransactionSerializer.toBytes(m)
    case m: PaymentTransaction =>
      PaymentTransaction.typeId +: PaymentTransactionSerializer.toBytes(m)
    case m: AddPubKeyInfoTransaction =>
      AddPubKeyInfoTransaction.typeId +: AddPubKeyInfoTransactionSerializer.toBytes(m)
    case m =>
      throw new Error(s"Serialization for unknown modifier: ${m.json.noSpaces}")
  }

  override def parseBytes(bytes: Array[Byte]): Try[EncryBaseTransaction] =
    Try(bytes.head).flatMap {
      case CoinbaseTransaction.`typeId` =>
        CoinbaseTransactionSerializer.parseBytes(bytes.tail)
      case PaymentTransaction.`typeId` =>
        PaymentTransactionSerializer.parseBytes(bytes.tail)
      case AddPubKeyInfoTransaction.`typeId` =>
        AddPubKeyInfoTransactionSerializer.parseBytes(bytes.tail)
      case m =>
        Failure(new Error(s"Deserialization for unknown type byte: $m"))
    }
}