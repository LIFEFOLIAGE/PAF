delete
from foliage2.flgbatch_ondemand_tab;

delete
from foliage2.flgpending_batch_tab;

delete
from foliage2.flgexecuted_batch_tab;

delete
from foliage2.flgerror_batch_tab;

delete 
from foliage2.flgreport_p1_2_tab;
	
delete 
from foliage2.flgreport_p3_tab;
	
delete 
from foliage2.flgreport_p4_tab;

update foliage2.flgbatch_scheduling_tab
	set data_partenza = case
	when intervallo_frequenza = '1 years'::interval then
		date'2025-01-01'
	else
		date'2024-09-01'
	end;
