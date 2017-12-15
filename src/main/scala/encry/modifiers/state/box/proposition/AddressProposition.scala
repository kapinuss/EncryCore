package encry.modifiers.state.box.proposition

import encry.crypto.Address
import scorex.core.serialization.Serializer
import scorex.core.transaction.box.proposition.PublicKey25519Proposition.{ChecksumLength, calcCheckSum}
import scorex.core.transaction.box.proposition.{Proposition, PublicKey25519Proposition}
import scorex.crypto.encode.Base58
import scorex.core.transaction.box.proposition.PublicKey25519Proposition._

import scala.util.{Failure, Success, Try}

// Holds the wallet address, which responds to some `publicKey`.
// Should be used with `scorex.core.transaction.box.proposition.PublicKey25519Proposition`.
case class AddressProposition(address: Address) extends Proposition {

  override type M = AddressProposition

  // TODO: Проверка соответствия адреса публичному ключу.
  def verify(proposition: PublicKey25519Proposition): Boolean = address == proposition.address

  override def serializer: Serializer[AddressProposition] = AddressPropositionSerializer

}

object AddressProposition {

  def addrBytes(address: Address): Array[Byte] = Base58.decode(address).get

  def validAddress(address: String): Boolean = {
    val addrBytes: Array[Byte] = Base58.decode(address).get
      if (addrBytes.length != AddressLength) false
      else {
        val checkSum = addrBytes.takeRight(ChecksumLength)
        val checkSumGenerated = calcCheckSum(addrBytes.dropRight(ChecksumLength))
        if (checkSum.sameElements(checkSumGenerated)) true
        else false
      }
    }
}

object AddressPropositionSerializer extends Serializer[AddressProposition] {

  override def toBytes(obj: AddressProposition): Array[Byte] = Base58.decode(obj.address).get

  override def parseBytes(bytes: Array[Byte]): Try[AddressProposition] = Try {
    new AddressProposition(Address @@ Base58.encode(bytes))
  }
}
