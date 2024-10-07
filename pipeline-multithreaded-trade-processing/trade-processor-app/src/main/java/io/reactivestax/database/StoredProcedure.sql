delimiter $$

create procedure trade_procedure(
        in accountNumber varchar(100),
        in securityCusip varchar(100),
        in direction varchar(100),
        in quantity int,
        in postedStatus varchar(100),
        in transactionTime Timestamp,
        in tradeId varchar(100),
        out error_code int
)
begin
         declare versionOfPosition int default 0;
         declare error int default 0;

         insert into journal_entry (account_number, security_cusip, direction, quantity, posted_status, transaction_time, trade_id)
         values (accountNumber,securityCusip,direction,quantity,postedStatus,transactionTime,tradeId);
         if(row_count > 0)

         if direction = 'sell' then
            set quantity = 0-quantity;
         end if;

         Update trade_payloads
         set je_status = 'posted'
         where trade_id = tradeId;

         Select version into versionOfPosition
         from positions
         where account_number = accountNumber and security_cusip = security_cusip;

         if versionOfPosition = 0 then
                Insert into positions (account_number, security_cusip, positions, version)
                values(accountNumber, securityCusip, quantity, 1);
         else
                Update positions
                set positions = positions + quantity, version = version+1
                where account_number = accountNumber and security_cusip = securityCusip and version = versionOfPosition;
         end if;

         Update journal_entry
         set posted_status = 'POSTED'
         where trade_id = tradeId;
end $$

delimiter ;


CALL trade_procedure(
    'TDB_CUST_9642277','GOOGL','sell',522,'not_posted','2024-09-20 17:48:03','TDB_00004002'
);

