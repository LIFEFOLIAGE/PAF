import { Injectable } from '@angular/core';

const buildMessage : ((msg: any) => any) = (msg: any) => {
  return {
    time: new Date(),
    msg
  };
}

const noOp : ((msg: any) => void) = (msg: any) => {
};

const loggers: any = {
  debug: (msg: any) => {
    console.debug(buildMessage(msg));
  },
  log: (msg: any) => {
    console.log(buildMessage(msg));
  },
  warn: (msg: any) => {
    console.warn(buildMessage(msg));
  },
  error: (msg: any) => {
    console.error(buildMessage(msg));
  }
};

const functionNames: string[] = [
  "debug", "log", "warn", "error"
]
const functionNamesLength = functionNames.length;

export enum LogLevel {
  Debug = 0,
  Info,
  Warning,
  Error
}

@Injectable({
  providedIn: 'root'
})
export class LoggingService {
  private _debug : ((msg: any) => void) = noOp;
  public get debug(): ((msg: any) => void) {
    return this._debug;
  } 
  private _log : ((msg: any) => void) = noOp;
  public get log(): ((msg: any) => void) {
    return this._log;
  } 
  private _warn : ((msg: any) => void) = noOp;
  public get warn(): ((msg: any) => void) {
    return this._warn;
  } 
  private _error : ((msg: any) => void) = noOp;
  public get error(): ((msg: any) => void) {
    return this._error;
  } 

  private _logLevel: LogLevel = LogLevel.Debug; 
  public set logLevel(value: LogLevel ) {
    const $this: any = this;
    for(let i = 0; i < value; i++) {
      const name = functionNames[i];
      $this[`_${name}`] = noOp;
    }
    for(let i = value; i < functionNamesLength; i++) {
      const name = functionNames[i];
      $this[`_${name}`] = loggers[name];
    }
    
  }
  public get logLevel(): LogLevel {
    return this._logLevel;
  }
}
