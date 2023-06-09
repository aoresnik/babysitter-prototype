export * from './commandExecutionResource.service';
import { CommandExecutionResourceService } from './commandExecutionResource.service';
export * from './commandsResource.service';
import { CommandsResourceService } from './commandsResource.service';
export * from './statusResource.service';
import { StatusResourceService } from './statusResource.service';
export const APIS = [CommandExecutionResourceService, CommandsResourceService, StatusResourceService];
