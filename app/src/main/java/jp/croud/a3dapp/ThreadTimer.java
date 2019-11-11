package jp.croud.a3dapp;


import android.util.Log;

interface ThreadAction
{
	public void onStart(Object object);

	public void onEnd(Object object);

	public void onRender(Object object);

	public void onAction(Object object);

	public void onIdel(Object object);
}

class ThreadTimer extends Thread
{
	ThreadAction threadAction;
	TimeCounter timeCounter = new TimeCounter();
	Thread m_thread;
	Object object;
	volatile boolean enable;
	volatile boolean enter;

	public boolean isEnable()
	{
		return enable;
	}
	public ThreadTimer()
	{
		threadAction = null;
		enable = false;
		enter = false;
	}
	public void setAction(ThreadAction action)
	{
		threadAction = action;
	}
	public void startThread(Object o)
	{
		stopThread();
		timeCounter.resetTime();
		enable = true;
		enter = false;
		object = o;
		start();
	}

	public void stopSignal()
	{
		enable = false;
	}

	public void stopThread()
	{
	   	Log.d("Thread","call stop");
		int i;
		enable = false;
		
		try
		{
			Thread.sleep(1000);

			for (i = 0; i < 3000 && enter; i++)
				Thread.sleep(10);
		} catch (InterruptedException e){}
		if(isAlive())
			stop();
	}
	public void setWait(boolean flag)
	{
		timeCounter.setEnable(flag);
	}
	public long getInterval()
	{
		return timeCounter.getTimeSync();
	}

	public void setInterval(long interval)
	{
		timeCounter.setTimeSync(interval);
	}

	public void run()
	{
		enter = true;

		if (threadAction != null)
			threadAction.onStart(object);
	   	Log.d("Thread","Start");
		while (enable)
		{
			synchronized (this)
			{

				int i;
				// 必要動作回数
				long count = timeCounter.getCount();

				// 実行処理用ループ
				if (enable && threadAction != null)
				{
					for (i = 0; enable && i < count; i++)
						threadAction.onAction(object);
				}
				// アイドル処理用
				if (enable && threadAction != null)
				{
					threadAction.onIdel(object);
				}
				// レンダリング用
				if (enable && threadAction != null)
				{
					// レンダリングの必要性を確認
					if (enable && (count > 0 || !timeCounter.isEnable()))
					{
						threadAction.onRender(object);
					}
				}
			}

			if (enable)
				timeCounter.sleep();
		}

		if (threadAction != null)
			threadAction.onEnd(object);
	   	Log.d("Thread","Stop");
		enter = false;
	}
}