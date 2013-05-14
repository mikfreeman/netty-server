package ie.freeman.dao.trade;

import ie.freeman.domain.trade.Trade;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("tradeDao")
public class TradeDaoImpl implements TradeDao
{
	@Autowired
	private SessionFactory sessionFactory;

	public Trade getTrade(String stockId)
	{
		return (Trade) this.sessionFactory.getCurrentSession().get(Trade.class,
				stockId);
	}

}
