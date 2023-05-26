import {Component, OnInit} from '@angular/core';
import {UITreeNode} from "primeng/tree";
import {TreeNode} from "primeng/api";

@Component({
  selector: 'app-app-commands-list-pane',
  templateUrl: './app-commands-list-pane.component.html',
  styleUrls: ['./app-commands-list-pane.component.css']
})
export class AppCommandsListPaneComponent implements OnInit {
  commands: TreeNode[] = [ {
    key: '0',
    label: 'Local commands',
    data: 'Documents Folder',
    icon: 'pi pi-fw pi-inbox',
    children: [
      { key: '0-0-0', label: 'test.sh', icon: 'pi pi-fw pi-file', data: 'Expenses Document' },
      { key: '0-0-1', label: 'test2.sh', icon: 'pi pi-fw pi-file', data: 'Resume Document' }
    ]
  } ];

  ngOnInit(): void {
    this.commands.push();
  }
}
