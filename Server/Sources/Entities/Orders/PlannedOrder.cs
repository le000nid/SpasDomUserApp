﻿using Entities.Orders.Base;
using System.ComponentModel.DataAnnotations.Schema;

namespace Entities.Orders
{
    [Table("Planned-Orders")]
    public class PlannedOrder : Order
    {
        public PlannedOrder()
        {
            Status = OrderStatus.Created;
            Category = null;
            Subcategory = null;
        }
        
        
        [ForeignKey(nameof(PlannedOrderCategory))]
        public long? CategoryId { get; set; }
        public PlannedOrderCategory Category { get; set; }
        
        
        [ForeignKey(nameof(PlannedOrderCategory))]
        public long? SubcategoryId { get; set; }
        public PlannedOrderCategory Subcategory { get; set; }
    }
}