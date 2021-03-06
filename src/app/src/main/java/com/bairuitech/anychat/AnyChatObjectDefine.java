package com.bairuitech.anychat;

public class AnyChatObjectDefine {
    public static final int ANYCHAT_AGENT_CTRL_EVALUATION = 605;
    public static final int ANYCHAT_AGENT_CTRL_FINISHSERVICE = 604;
    public static final int ANYCHAT_AGENT_CTRL_SERVICEREQUEST = 602;
    public static final int ANYCHAT_AGENT_CTRL_SERVICESTATUS = 601;
    public static final int ANYCHAT_AGENT_CTRL_STARTSERVICE = 603;
    public static final int ANYCHAT_AGENT_EVENT_ISREADY = 604;
    public static final int ANYCHAT_AGENT_EVENT_SERVICENOTIFY = 602;
    public static final int ANYCHAT_AGENT_EVENT_STATUSCHANGE = 601;
    public static final int ANYCHAT_AGENT_EVENT_WAITINGUSER = 603;
    public static final int ANYCHAT_AGENT_INFO_RELATEQUEUES = 607;
    public static final int ANYCHAT_AGENT_INFO_SERVICEBEGINTIME = 603;
    public static final int ANYCHAT_AGENT_INFO_SERVICESTATUS = 601;
    public static final int ANYCHAT_AGENT_INFO_SERVICETOTALNUM = 605;
    public static final int ANYCHAT_AGENT_INFO_SERVICETOTALTIME = 604;
    public static final int ANYCHAT_AGENT_INFO_SERVICEUSERID = 602;
    public static final int ANYCHAT_AGENT_INFO_SERVICEUSERINFO = 606;
    public static final int ANYCHAT_AGENT_STATUS_CLOSEED = 0;
    public static final int ANYCHAT_AGENT_STATUS_OFFLINE = 10;
    public static final int ANYCHAT_AGENT_STATUS_PAUSED = 3;
    public static final int ANYCHAT_AGENT_STATUS_WAITTING = 1;
    public static final int ANYCHAT_AGENT_STATUS_WORKING = 2;
    public static final int ANYCHAT_AREA_CTRL_USERENTER = 401;
    public static final int ANYCHAT_AREA_CTRL_USERLEAVE = 402;
    public static final int ANYCHAT_AREA_EVENT_ENTERRESULT = 402;
    public static final int ANYCHAT_AREA_EVENT_LEAVERESULT = 405;
    public static final int ANYCHAT_AREA_EVENT_STATUSCHANGE = 401;
    public static final int ANYCHAT_AREA_EVENT_USERENTER = 403;
    public static final int ANYCHAT_AREA_EVENT_USERLEAVE = 404;
    public static final int ANYCHAT_AREA_INFO_AGENTCOUNT = 401;
    public static final int ANYCHAT_AREA_INFO_AGENTIDLIST = 405;
    public static final int ANYCHAT_AREA_INFO_BUSYAGENTCOUNT = 410;
    public static final int ANYCHAT_AREA_INFO_GUESTCOUNT = 402;
    public static final int ANYCHAT_AREA_INFO_IDLEAGENTCOUNT = 406;
    public static final int ANYCHAT_AREA_INFO_QUEUECOUNT = 404;
    public static final int ANYCHAT_AREA_INFO_QUEUEUSERCOUNT = 403;
    public static final int ANYCHAT_AREA_INFO_STATUSJSON = 407;
    public static final int ANYCHAT_AREA_INFO_WAITINGCOUNT = 408;
    public static final int ANYCHAT_AREA_INFO_WORKAGENTCOUNT = 409;
    public static final int ANYCHAT_INVALID_OBJECT_ID = -1;
    public static final int ANYCHAT_OBJECT_CTRL_CREATE = 2;
    public static final int ANYCHAT_OBJECT_CTRL_DEBUGOUTPUT = 4;
    public static final int ANYCHAT_OBJECT_CTRL_DELETE = 5;
    public static final int ANYCHAT_OBJECT_CTRL_MODIFY = 6;
    public static final int ANYCHAT_OBJECT_CTRL_SYNCDATA = 3;
    public static final int ANYCHAT_OBJECT_EVENT_STATISTICS = 3;
    public static final int ANYCHAT_OBJECT_EVENT_SYNCDATAFINISH = 2;
    public static final int ANYCHAT_OBJECT_EVENT_UPDATE = 1;
    public static final int ANYCHAT_OBJECT_FLAGS_AGENT = 2;
    public static final int ANYCHAT_OBJECT_FLAGS_AREAUSERINFO = 1024;
    public static final int ANYCHAT_OBJECT_FLAGS_AUTOMODE = 16;
    public static final int ANYCHAT_OBJECT_FLAGS_CLIENT = 0;
    public static final int ANYCHAT_OBJECT_FLAGS_CONNECT = 128;
    public static final int ANYCHAT_OBJECT_FLAGS_GLOBAL = 64;
    public static final int ANYCHAT_OBJECT_FLAGS_GUESTLOGIN = 32;
    public static final int ANYCHAT_OBJECT_FLAGS_MANANGER = 4;
    public static final int ANYCHAT_OBJECT_FLAGS_MANUALSYNCAREA = 2048;
    public static final int ANYCHAT_OBJECT_FLAGS_MULTICHANNEL = 256;
    public static final int ANYCHAT_OBJECT_FLAGS_QUEUEUSERLIST = 512;
    public static final int ANYCHAT_OBJECT_INFO_ATTRIBUTE = 10;
    public static final int ANYCHAT_OBJECT_INFO_DESCRIPTION = 11;
    public static final int ANYCHAT_OBJECT_INFO_FLAGS = 7;
    public static final int ANYCHAT_OBJECT_INFO_GUID = 14;
    public static final int ANYCHAT_OBJECT_INFO_INTTAG = 12;
    public static final int ANYCHAT_OBJECT_INFO_NAME = 8;
    public static final int ANYCHAT_OBJECT_INFO_PRIORITY = 9;
    public static final int ANYCHAT_OBJECT_INFO_STATISTICS = 17;
    public static final int ANYCHAT_OBJECT_INFO_STATUSJSON = 15;
    public static final int ANYCHAT_OBJECT_INFO_STRINGID = 16;
    public static final int ANYCHAT_OBJECT_INFO_STRINGTAG = 13;
    public static final int ANYCHAT_OBJECT_TYPE_AGENT = 6;
    public static final int ANYCHAT_OBJECT_TYPE_AREA = 4;
    public static final int ANYCHAT_OBJECT_TYPE_CLIENTUSER = 8;
    public static final int ANYCHAT_OBJECT_TYPE_QUEUE = 5;
    public static final int ANYCHAT_OBJECT_TYPE_QUEUEGROUP = 10;
    public static final int ANYCHAT_OBJECT_TYPE_SKILL = 9;
    public static final int ANYCHAT_QUEUE_CTRL_USERENTER = 501;
    public static final int ANYCHAT_QUEUE_CTRL_USERLEAVE = 502;
    public static final int ANYCHAT_QUEUE_EVENT_ENTERRESULT = 502;
    public static final int ANYCHAT_QUEUE_EVENT_LEAVERESULT = 505;
    public static final int ANYCHAT_QUEUE_EVENT_STARTSERVICE = 506;
    public static final int ANYCHAT_QUEUE_EVENT_STATUSCHANGE = 501;
    public static final int ANYCHAT_QUEUE_EVENT_USERENTER = 503;
    public static final int ANYCHAT_QUEUE_EVENT_USERINFOLISTCHG = 507;
    public static final int ANYCHAT_QUEUE_EVENT_USERLEAVE = 504;
    public static final int ANYCHAT_QUEUE_INFO_AGENTINFO = 509;
    public static final int ANYCHAT_QUEUE_INFO_BEFOREUSERNUM = 502;
    public static final int ANYCHAT_QUEUE_INFO_LENGTH = 504;
    public static final int ANYCHAT_QUEUE_INFO_MYENTERQUEUETIME = 503;
    public static final int ANYCHAT_QUEUE_INFO_MYSEQUENCENO = 501;
    public static final int ANYCHAT_QUEUE_INFO_USERIDLIST = 510;
    public static final int ANYCHAT_QUEUE_INFO_USERINFOLIST = 512;
    public static final int ANYCHAT_QUEUE_INFO_WAITINGTIMELIST = 511;
    public static final int ANYCHAT_QUEUE_INFO_WAITTIMESECOND = 508;
}
