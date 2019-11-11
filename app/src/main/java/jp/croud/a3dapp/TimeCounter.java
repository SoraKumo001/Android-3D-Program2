package jp.croud.a3dapp;



public class TimeCounter
{
	public TimeCounter()
	{
		enable = true;
		syncTime = 10;
		resetTime();
	}

	public void resetTime()
	{
		nowCount = 0;
		startTime = System.currentTimeMillis();
	}

	public void setTimeSync(long t)
	{
		syncTime = t;
		resetTime();
	}

	public long getTime()
	{
		return System.currentTimeMillis() - startTime;
	}

	public long getCount()
	{
		// ウエイト無効のスキップ処理
		/*
		 * if(!m_bEnable) { m_iNowCount = getTime() / m_dwSyncTime;
		 * m_dwSleepTime = 0; return 1; }
		 */
		// 通常処理
		long oldCount;
		long t;
		long count;

		oldCount = nowCount;
		t = getTime();
		if (syncTime != 0)
			nowCount = t / syncTime;
		else
			return 1;
		count = nowCount - oldCount;

		if (count == 0)
			sleepTime = t % syncTime;
		else
			sleepTime = 0;
		return count;
	}

	public void sleep()
	{
		try
		{
			if (enable)
				Thread.sleep(sleepTime);
			else
				Thread.sleep(0);
		} catch (InterruptedException e)
		{
		}
	}

	public long sleepTime()
	{
		if (enable && syncTime != 0)
			return sleepTime;
		return 0;
	}

	public void setEnable(boolean flag)
	{
		enable = flag;
	}

	long getTimeSync()
	{
		return syncTime;
	}

	boolean isEnable()
	{
		return enable;
	}

	long syncTime;
	long startTime;
	long nowCount;
	long sleepTime;
	boolean enable;
}
