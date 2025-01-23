CREATE OR REPLACE FUNCTION foliage2.flgdelete_base64_formio_file()
 RETURNS trigger
 LANGUAGE plpgsql
AS $function$
declare
	nome_campo varchar;
	old_record json;
	valore_str varchar;
	valore_int int;
begin
	nome_campo = TG_ARGV[0];
	RAISE NOTICE 'nome_campo: %', nome_campo;
	old_record = to_json(old);
	RAISE NOTICE 'old_record: %', old_record;
	valore_str = old_record->>nome_campo;
	RAISE NOTICE 'valore_str: %', valore_str;
	if (valore_str is not null) then
		valore_int = valore_str::int;
		RAISE NOTICE 'valore_int: %', valore_int;
		delete from foliage2.flgbase64_formio_file_master_tab f
		where f.id_file = valore_int;
	end if;
	return old;
end;
$function$
;
