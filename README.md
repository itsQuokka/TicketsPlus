# Looks like you stumbled upon TicketsPlus!

This plugin is orientated for staff teams with players who might have difficulty in-game and require assistance!

The ability to clear out the chat from upset players, and have them all taken care of by a Ticket System is the best way to go!

Have your staff members assign themselves to tickets, leave comments, close and delete tickets at the ease of a command!

To be even cooler, manage your tickets with the simplicity of a GUI!

# Permissions:
  ### ticket.admin.*:
    children:
      - ticket.staff.*
      - ticket.notify
      - ticket.notifytoggle
      - ticket.debug
      - ticket.notify.all
      - ticket.forcedelete
  ### ticket.staff.*:
    children:
      - ticket.notify
      - ticket.teleport
      
# Permission Descriptions
  ##### ticket.notify.all:
    Recieve all the notifications of staff member ticket activity including: assigned, comments, closed, deleted, opened etc.. 
  ##### ticket.notify:
    The general permission for a staff member to be able to see a new incoming ticket!
  ##### ticket.notifytoggle:
    The permission to turn off all notifications of incoming ticket creations, and updates.
  ##### ticket.debug:
    The permission to be able to check if there are any errors on a ticket.
  ##### ticket.assign:
    The permission to assign yourself to a ticket, and be the one in-charge of leaving comments.
  ##### ticket.teleport:
    The permission to be able to teleport to the location of the created ticket through the GUI.
  ##### ticket.claim:
    The permission to be able to claim a new incoming ticket as long as there are no current assignee's.
