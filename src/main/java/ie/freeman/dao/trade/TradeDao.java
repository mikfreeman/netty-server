package ie.freeman.dao.trade;

import ie.freeman.domain.trade.Trade;

public interface TradeDao
{
	Trade getTrade(String stockId);
}
