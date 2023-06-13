package com.zfoo.thread;

import com.zfoo.thread.enums.ThreadGroupEnum;

import java.util.concurrent.Executor;

public  interface IThreadExecutor extends Executor {
    /**
     * �߳����ַָ��
     */
    String NAME_LIMIT = "_";

    /**
     * �ر��̳߳�
     */
    void shutdown();

    /**
     * �̳߳س��ж�Ӧִ��������߳�id��ȵ�ִ����
     */
    default Executor runThread(long threadId, long key) {return null;}

    /**
     * �̳߳ض�Ӧ����ö��
     */
    ThreadGroupEnum threadGroup();

    }