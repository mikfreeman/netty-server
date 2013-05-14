package ie.freeman.service.trade;

import ie.freeman.dao.trade.TradeDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("tradeService")
public class TradeServiceImpl implements TradeService
{
	@Autowired
	private TradeDao tradeDao;

	@Transactional(readOnly = true)
	public String getPrice(String stockId)
	{
		return tradeDao.getTrade(stockId).getPrice();
	}

}
