import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-compila-istanza',
  templateUrl: './compila-istanza.component.html',
  styleUrls: ['./compila-istanza.component.css']
})
export class CompilaIstanzaComponent implements OnInit{
  codIstanza?: string;
  isReadOnly: boolean = true;
  constructor (
    private route: ActivatedRoute
  ) {

  }
  ngOnInit(): void {
    this.codIstanza =  this.route.snapshot.params["codIstanza"];
    this.isReadOnly = this.route.snapshot.data["isReadOnly"]??true;
  }
}
