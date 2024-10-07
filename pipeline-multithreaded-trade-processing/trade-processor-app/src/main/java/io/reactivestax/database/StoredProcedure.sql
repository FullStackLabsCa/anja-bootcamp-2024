delimiter $$

create procedure trade_procedure(
        in accountNumber varchar(100),
        in securityCusip varchar(100),
        in direction varchar(100),
        in quantity int,
        in postedStatus varchar(100),
        in transactionTime Timestamp,
        in tradeId varchar(100),
        out errorCode int,
        out errorMessage varchar(255)
)
begin
         declare versionOfPosition int default 0;

         declare exit handler for sqlexception
         begin
                 set errorCode = 1;
                 set errorMessage = 'An error occurred during execution.';
         end;

         set errorCode = 0;
         set errorMessage = '';

         begin
                 declare continue handler for sqlexception
                 begin
                     set errorCode = 2;
                     set errorMessage = 'Error inserting into journal_entry.';
                     resignal;
                 end;
                 insert into journal_entry (account_number, security_cusip, direction, quantity, posted_status, transaction_time, trade_id)
                 values (accountNumber,securityCusip,direction,quantity,postedStatus,transactionTime,tradeId);
         end;

         if direction = 'sell' then
            set quantity = 0-quantity;
         end if;

         begin
                 declare continue handler for sqlexception
                 begin
                       set errorCode = 3;
                       set errorMessage = 'Error updating into trade_payloads.';
                       resignal;
                 end;
                 Update trade_payloads
                 set je_status = 'posted'
                 where trade_id = tradeId;
         end;


        begin
                 declare continue handler for sqlexception
                 begin
                      set errorCode = 4;
                      set errorMessage = 'Error selecting version from positions.';
                      resignal;
                 end;
                 Select version into versionOfPosition
                 from positions
                 where account_number = accountNumber and security_cusip = security_cusip;
        end;

         if versionOfPosition = 0 then
                begin
                        declare continue handler for sqlexception
                        begin
                             set errorCode = 5;
                             set errorMessage = 'Error inserting into positions table';
                             resignal;
                        end;
                        Insert into positions (account_number, security_cusip, positions, version)
                        values(accountNumber, securityCusip, quantity, 1);
                end;
         else
                begin
                      declare continue handler for sqlexception
                      begin
                           set errorCode = 6;
                           set errorMessage = 'Error updating positions table';
                           resignal;
                      end;
                      Update positions
                            set positions = positions + quantity, version = version+1
                            where account_number = accountNumber and security_cusip = securityCusip and version = versionOfPosition;
                end;
         end if;

         begin
               declare continue handler for sqlexception
               begin
                     set errorCode = 7;
                     set errorMessage = 'Error updating journal_entry table';
                     resignal;
               end;
               Update journal_entry
               set posted_status = 'POSTED'
               where trade_id = tradeId;
         end;
end $$

delimiter ;


CALL trade_procedure(
    'TDB_CUST_9642277','GOOGL','sell',522,'not_posted','2024-09-20 17:48:03','TDB_00004002'
);

