package cemfreitas.autorizadorMVC;

import java.io.Serializable;
import java.math.BigDecimal;

/* POJO Class used as value object.
 * It holds a transaction displayed on the JTable
 */
public class TransactionData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7251166872264246926L;

	private int status;
	private String data;
	private String codigo;
	private String processo;
	private BigDecimal valor;
	private String NSU;
	private String estabelecimento;
	private String numCartao;

	public TransactionData() {

	}

	public TransactionData(int status, String data, String codigo, String processo, BigDecimal valor, String NSU,
			String estabelecimento, String numCartao) {
		this.status = status;
		this.data = data;
		this.codigo = codigo;
		this.processo = processo;
		this.valor = valor;
		this.NSU = NSU;
		this.estabelecimento = estabelecimento;
		this.numCartao = numCartao;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getProcesso() {
		return processo;
	}

	public void setProcesso(String processo) {
		this.processo = processo;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		if (valor == null) {
			this.valor = new BigDecimal(0);
		} else {
			this.valor = valor.setScale(2, BigDecimal.ROUND_HALF_EVEN);
		}
	}

	public void setValor(double doubleValor) {
		this.valor = new BigDecimal(doubleValor);
		this.valor = valor.setScale(2, BigDecimal.ROUND_HALF_EVEN);
	}

	public String getNSU() {
		return NSU;
	}

	public void setNSU(String NSU) {
		this.NSU = NSU;
	}

	public String getEstabelecimento() {
		return estabelecimento;
	}

	public void setEstabelecimento(String estabelecimento) {
		this.estabelecimento = estabelecimento;
	}

	public String getNumCartao() {
		return numCartao;
	}

	public void setNumCartao(String numCartao) {
		this.numCartao = numCartao;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((estabelecimento == null) ? 0 : estabelecimento.hashCode());
		result = prime * result + ((NSU == null) ? 0 : NSU.hashCode());
		result = prime * result + ((numCartao == null) ? 0 : numCartao.hashCode());
		result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + ((processo == null) ? 0 : processo.hashCode());
		result = prime * result + status;
		result = prime * result + ((valor == null) ? 0 : valor.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TransactionData other = (TransactionData) obj;
		if (estabelecimento == null) {
			if (other.estabelecimento != null)
				return false;
		} else if (!estabelecimento.equals(other.estabelecimento))
			return false;
		if (NSU == null) {
			if (other.NSU != null)
				return false;
		} else if (!NSU.equals(other.NSU))
			return false;
		if (numCartao == null) {
			if (other.numCartao != null)
				return false;
		} else if (!numCartao.equals(other.numCartao))
			return false;
		if (codigo == null) {
			if (other.codigo != null)
				return false;
		} else if (!codigo.equals(other.codigo))
			return false;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		if (processo == null) {
			if (other.processo != null)
				return false;
		} else if (!processo.equals(other.processo))
			return false;
		if (status != other.status)
			return false;
		if (valor == null) {
			if (other.valor != null)
				return false;
		} else if (!valor.equals(other.valor))
			return false;
		return true;
	}

}
