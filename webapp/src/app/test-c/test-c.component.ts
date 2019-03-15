import { Component, OnInit } from '@angular/core';
import {DomSanitizer} from '@angular/platform-browser';

@Component({
  selector: 'app-test-c',
  templateUrl: './test-c.component.html',
  styleUrls: ['./test-c.component.scss']
})
export class TestCComponent implements OnInit {

  url: string;

  constructor(private sanitizer: DomSanitizer) { }

  ngOnInit() {
  }

  updateUrl(value: any) {
    console.log(value);
    this.url = value;
  }

  onChangeUrl(event: Event, value: string) {
    console.log(value);
    let url: any;
    if (value.trim().length === 0) {
      url = null;
    } else {
      url = this.sanitizer.bypassSecurityTrustResourceUrl(value);
      console.log(url);
    }
    this.url = url;

  }

}
